package org.mulesoft.als.server.modules.workspace.resolution

import java.util.UUID
import amf.core.model.document.BaseUnit
import amf.plugins.document.webapi.model.{Extension, Overlay}
import org.mulesoft.als.configuration.WorkspaceConfiguration
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.workspace.{ProcessingFile, Repository, ResolverStagingArea, StagingArea}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.{UnitTaskManager, UnitWorkspaceManager, UnitsManager}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

class ResolutionTaskManager(telemetryProvider: TelemetryProvider,
                            logger: Logger,
                            environmentProvider: EnvironmentProvider,
                            private val allSubscribers: List[ResolvedUnitListener],
                            override val dependencies: List[AccessUnits[AmfResolvedUnit]])
    extends UnitTaskManager[AmfResolvedUnit, AmfResolvedUnit, BaseUnitListenerParams]
    with UnitsManager[AmfResolvedUnit, AmfResolvedUnit]
    with BaseUnitListener {

  override def subscribers: List[AstListener[AmfResolvedUnit]] =
    allSubscribers.filter(_.isActive)

  override protected val stagingArea: StagingArea[BaseUnitListenerParams] =
    new ResolverStagingArea()

  override protected val repository: Repository[AmfResolvedUnit] =
    new ResolutionRepository()

  override protected def log(msg: String): Unit =
    logger.error(msg, "ResolutionTaskManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future.unit

  override protected def processTask(): Future[Unit] = Future {
    val uuid          = UUID.randomUUID().toString
    val (uri, params) = stagingArea.dequeue()
    changeState(ProcessingFile)

    val resolvedInstance =
      AmfResolvedUnitImpl(params.parseResult.baseUnit, params.diagnosticsBundle, params.workspaceConfiguration)

    if (isInMainTree(uri)) {
      params.parseResult.tree.foreach { u =>
        logger.debug(s"Replacing $u with unit resolved from $uri", "ResolutionTaskManager", "processTask")
        repository
          .updateUnit(u, resolvedInstance) // every dependency should be updated
      }
    }
    logger.debug(s"Updating $uri unit", "ResolutionTaskManager", "processTask")
    repository.updateUnit(uri, resolvedInstance)
    subscribers.foreach(_.onNewAst(resolvedInstance, uuid))
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
                .getOrElse(throw new Exception("Unit not found"))
            }
          case None =>
            getNext(uri).getOrElse(throw new Exception("Unit not found"))
        }
    }

  override def getLastUnit(uri: String, uuid: String): Future[AmfResolvedUnit] =
    unitAccessor match {
      case Some(ua) =>
        ua.getLastUnit(uri, uuid)
          .flatMap(_.getLast)
          .flatMap(_ => getUnit(uri, uuid).flatMap(_.getLast)) // double check after resolved that last is still ua's last?
          .andThen {
            case Failure(value) =>
              logger.error(
                Option(value).flatMap(v => Option(v.getMessage)).getOrElse(s"error while getting unit $uri"),
                "ResolutionTaskManager",
                "getLastUnit")
          }
      case None => getUnit(uri, uuid).flatMap(_.getLast)
    }

  override def onNewAst(ast: BaseUnitListenerParams, uuid: String): Unit =
    stage(ast.parseResult.location, ast)

  override def onRemoveFile(uri: String): Unit = {
    repository.removeUnit(uri)
    subscribers.foreach(_.onRemoveFile(uri))
  }

  case class AmfResolvedUnitImpl(override val originalUnit: BaseUnit,
                                 override val diagnosticsBundle: Map[String, DiagnosticsBundle],
                                 override val workspaceConfiguration: Option[WorkspaceConfiguration])
      extends AmfResolvedUnit {
    private val uri: String = originalUnit.identifier

    override protected type T = AmfResolvedUnit

    override protected def resolvedUnitFn(): Future[BaseUnit] = {
      telemetryProvider
        .timeProcess("AMF RESOLVE",
                     MessageTypes.BEGIN_RESOLUTION,
                     MessageTypes.END_RESOLUTION,
                     "resolving with editing pipeline",
                     uri,
                     innerResolveUnit)
    }

    private def innerResolveUnit() =
      Future(
        environmentProvider.amfConfiguration
          .modelBuilder()
          .fullResolution(originalUnit.cloneUnit(), eh))

    override def next: Option[Future[T]] = getNext(uri)
  }

  override def isInMainTree(uri: String): Boolean =
    unitAccessor.exists(_.isInMainTree(uri))

  // Initialize after construction
  init()
}
