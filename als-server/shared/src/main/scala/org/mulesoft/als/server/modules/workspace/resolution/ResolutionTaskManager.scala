package org.mulesoft.als.server.modules.workspace.resolution

import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.workspace._
import org.mulesoft.als.server.workspace.{UnitTaskManager, UnitsManager}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, AMLSpecificConfiguration}
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle, ValidationProfile}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

class ResolutionTaskManager private (
    telemetryProvider: TelemetryProvider,
    logger: Logger,
    private val allSubscribers: List[ResolvedUnitListener],
    override val dependencies: List[AccessUnits[AmfResolvedUnit]]
) extends UnitTaskManager[AmfResolvedUnit, AmfResolvedUnit, BaseUnitListenerParams]
    with UnitsManager[AmfResolvedUnit, AstListener[AmfResolvedUnit]]
    with BaseUnitListener {

  override def subscribers: List[AstListener[AmfResolvedUnit]] =
    allSubscribers.filter(_.isActive)

  override protected val stagingArea: StagingArea[BaseUnitListenerParams] =
    new ResolverStagingArea()

  override protected val repository: Repository[AmfResolvedUnit] =
    new ResolutionRepository()

  override protected def log(msg: String, isError: Boolean = false): Unit =
    if (isError)
      logger.error(msg, "ResolutionTaskManager", "Processing request")
    else logger.debug(msg, "ResolutionTaskManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future.unit

  override protected def processTask(): Future[Unit] = {
    changeState(ProcessingFile)
    val uuid          = UUID.randomUUID().toString
    val (uri, params) = stagingArea.dequeue()

    val resolvedInstance =
      AmfResolvedUnitImpl(
        params.parseResult.result.baseUnit,
        params.diagnosticsBundle,
        params.parseResult.context.state
      )
    isInMainTree(uri).map { isMainTree =>
      if (isMainTree) {
        params.parseResult.tree.foreach { u =>
          logger.debug(s"Replacing $u with unit resolved from $uri", "ResolutionTaskManager", "processTask")
          repository
            .updateUnit(u, resolvedInstance) // every dependency should be updated
        }
      }
      addProfileIfNotPresent(params.parseResult.context.state)
      logger.debug(s"Updating $uri unit", "ResolutionTaskManager", "processTask")
      repository.updateUnit(uri, resolvedInstance)
      // prevents notifying diagnostics on dependencies
      if (!params.isDependency) subscribers.foreach(_.onNewAst(resolvedInstance, uuid))
    }
  }

  def addProfileIfNotPresent(state: ALSConfigurationState): Unit = {
    val newProfiles = state.profiles.filterNot(p => repository.getAllFilesUris.contains(p.path))
    newProfiles.foreach { p =>
      repository.updateUnit(p.path, ProfileResolvedUnit(p, state))
    }
    // TODO: check hot reload to always override editor state profiles
  }

  override protected def toResult(uri: String, unit: AmfResolvedUnit): AmfResolvedUnit = unit

  override def getUnit(uri: String, uuid: String): Future[AmfResolvedUnit] =
    repository.getUnit(uri) match {
      case Some(r) => Future.successful(r)
      case _ =>
        unitAccessor match {
          case Some(ua) =>
            ua.getUnit(uri, uuid).flatMap { _ =>
              repository
                .getUnit(uri)
                .map(Future.successful)
                .orElse(getNext(uri))
                .getOrElse(throw UnitNotFoundException(uri, uuid))
            }
          case None =>
            getNext(uri).getOrElse(throw UnitNotFoundException(uri, uuid))
        }
    }

  override def getLastUnit(uri: String, uuid: String): Future[AmfResolvedUnit] =
    unitAccessor match {
      case Some(ua) =>
        ua.getLastUnit(uri, uuid)
          .flatMap(_.getLast)
          .flatMap(_ =>
            getUnit(uri, uuid).flatMap(_.getLast)
          ) // double check after resolved that last is still ua's last?
          .andThen { case Failure(value) =>
            logger.error(
              Option(value).flatMap(v => Option(v.getMessage)).getOrElse(s"error while getting unit $uri"),
              "ResolutionTaskManager",
              "getLastUnit"
            )
          }
      case None => getUnit(uri, uuid).flatMap(_.getLast)
    }

  override def onNewAst(ast: BaseUnitListenerParams, uuid: String): Future[Unit] = synchronized {
    logger.debug(s"Got new AST: ${ast.parseResult.result.baseUnit.identifier}", "ResolutionTaskManager", "onNewAst")
    logger.debug(s"state: $state", "ResolutionTaskManager", "onNewAst")
    logger.debug(s"pending: ${stagingArea.hasPending}", "ResolutionTaskManager", "onNewAst")
    stage(ast.parseResult.location, ast)
  }

  override def onRemoveFile(uri: String): Unit = {
    repository.removeUnit(uri)
    subscribers.foreach(_.onRemoveFile(uri))
  }

  case class AmfResolvedUnitImpl(
      override val baseUnit: BaseUnit,
      override val diagnosticsBundle: Map[String, DiagnosticsBundle],
      override val alsConfigurationState: ALSConfigurationState
  ) extends AmfResolvedUnit {
    private val uri: String = baseUnit.identifier

    override protected type T = AmfResolvedUnit

    override protected def resolvedUnitFn(): Future[AMFResult] = {
      telemetryProvider
        .timeProcess(
          "AMF RESOLVE",
          MessageTypes.BEGIN_RESOLUTION,
          MessageTypes.END_RESOLUTION,
          "resolving with editing pipeline",
          uri,
          innerResolveUnit
        )
    }

    private def innerResolveUnit(): Future[AMFResult] =
      Future(configuration.fullResolution(baseUnit))

    override def next: Option[Future[T]] = getNext(uri)

    override val configuration: AMLSpecificConfiguration = alsConfigurationState.configForUnit(baseUnit)
  }

  case class ProfileResolvedUnit(vp: ValidationProfile, val alsConfigurationState: ALSConfigurationState)
      extends AmfResolvedUnit {

    override protected def resolvedUnitFn(): Future[AMFResult] =
      Future.successful(alsConfigurationState.editorState.getAmlConfig.baseUnitClient().transform(vp.model))

    override val diagnosticsBundle: Map[String, DiagnosticsBundle] = Map.empty
    override val baseUnit: BaseUnit                                = vp.model

    override def next: Option[Future[AmfResolvedUnit]] = None
  }

  override def isInMainTree(uri: String): Future[Boolean] =
    unitAccessor
      .map(_.isInMainTree(uri))
      .getOrElse(Future.successful(false))
}

object ResolutionTaskManager {
  def apply(
      telemetryProvider: TelemetryProvider,
      logger: Logger,
      allSubscribers: List[ResolvedUnitListener],
      dependencies: List[AccessUnits[AmfResolvedUnit]]
  ): ResolutionTaskManager = {
    val manager =
      new ResolutionTaskManager(telemetryProvider, logger, allSubscribers, dependencies)
    manager.init()
    manager
  }
}
