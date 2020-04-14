package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.{WorkspaceConf, WorkspaceConfigurationProvider}
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.amfmanager.BaseUnitImplicits._
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class WorkspaceContentManager(val folder: String,
                              environmentProvider: EnvironmentProvider,
                              telemetryProvider: TelemetryProvider,
                              logger: Logger,
                              dependencies: List[BaseUnitListener]) {

  private var state: WorkspaceState                 = Idle
  private var configMainFile: Option[WorkspaceConf] = None

  def workspaceConfiguration: Option[WorkspaceConf] = configMainFile

  def setConfigMainFile(workspaceConf: Option[WorkspaceConf]): Unit = {
    repository.cleanTree()
    repository.setCachables(workspaceConf.map(_.cachables).getOrElse(Set.empty))
    configMainFile = workspaceConf
  }

  def mainFile: Option[String] = configMainFile.map(_.mainFile)

  def configFile: Option[String] =
    configMainFile.flatMap(ic => ic.configReader.map(cr => s"${ic.rootFolder}/${cr.configFileName}"))

  private val stagingArea: StagingArea                                               = new StagingArea(environmentProvider)
  def environment: Environment                                                       = stagingArea.snapshot().environment
  private val repository                                                             = new Repository(logger)
  private var current: Future[Unit]                                                  = Future.unit
  private var workspaceConfigurationProvider: Option[WorkspaceConfigurationProvider] = None

  def canProcess: Boolean = state == Idle && current == Future.unit

  def changedFile(uri: String, kind: NotificationKind): Unit = synchronized {
    if (state == NotAvailable) throw new UnavailableWorkspaceException
    stagingArea.enqueue(uri, kind)
    if (canProcess) current = process()
  }

  def getCompilableUnit(uri: String): Future[CompilableUnit] = {
    val encodedUri = FileUtils.getEncodedUri(uri, environmentProvider.platform)
    repository.getParsed(encodedUri) match {
      case Some(pu) =>
        Future.successful(getCurrentCU(encodedUri, pu))
      case _ => getNext(encodedUri).getOrElse(fail(encodedUri))
    }
  }

  private def getCurrentCU(encodedUri: String, pu: ParsedUnit): CompilableUnit =
    pu.toCU(getNext(encodedUri), mainFile, repository.getReferenceStack(encodedUri), state == NotAvailable)

  private def next(f: Future[Unit]): Future[Unit] = {
    f.recoverWith({
        case e =>
          logger.error(e.getMessage, "WorkspaceContentManager", "Processing request")
          Future.successful(Unit)
      })
      .map { u =>
        current = process()
        u
      }
  }

  private def process(): Future[Unit] = {
    if (state == NotAvailable) throw new UnavailableWorkspaceException
    else if (stagingArea.hasPending) next(preprocessSnapshot())
    else goIdle()
  }

  def withConfiguration(confProvider: WorkspaceConfigurationProvider): WorkspaceContentManager = {
    workspaceConfigurationProvider = Some(confProvider)
    this
  }

  private def preprocessSnapshot(): Future[Unit] = {
    if (stagingArea.shouldDie) disable()
    else processSnapshot()

  }

  private def processSnapshot(): Future[Unit] = {
    val snapshot: Snapshot    = stagingArea.snapshot()
    val (treeUnits, isolated) = snapshot.files.partition(u => repository.inTree(u._1)) // what if a new file is added between the partition and the override down
    val changedTreeUnits =
      treeUnits.filter(tu => tu._2 == CHANGE_FILE || tu._2 == CLOSE_FILE)

    if (hasChangedConfigFile(snapshot)) processChangeConfigChanges(snapshot)
    else if (changedTreeUnits.nonEmpty)
      processMFChanges(configMainFile.get.mainFile, snapshot)
    else
      processIsolatedChanges(isolated, snapshot.environment)
  }

  private def hasChangedConfigFile(snapshot: Snapshot) =
    snapshot.files.map(_._2).contains(CHANGE_CONFIG)

  private def fail(uri: String) = throw UnitNotFoundException(uri)

  private def getNext(uri: String): Option[Future[CompilableUnit]] =
    current match {
      case Future.unit => None
      case _           => Some(current.flatMap(_ => getCompilableUnit(uri)))
    }

  private def goIdle(): Future[Unit] = {
    changeState(Idle)
    Future.unit
  }

  private def changeState(newState: WorkspaceState): Unit = synchronized {
    if (state == NotAvailable) throw new UnavailableWorkspaceException
    state = newState
  }

  private val isDisabled = Promise[Unit]()

  private def disable(): Future[Unit] = {
    changeState(NotAvailable)
    Future(dependencies.map(d => repository.getAllFilesUris.foreach(d.onRemoveFile)))
      .map(_ => isDisabled.success())
  }

  private def processIsolatedChanges(files: List[(String, NotificationKind)], environment: Environment): Future[Unit] = {
    val (closedFiles, changedFiles) = files.partition(_._2 == CLOSE_FILE)
    cleanFiles(closedFiles)

    if (changedFiles.nonEmpty)
      processIsolated(files.head._1, environment, UUID.randomUUID().toString)
    else Future.successful(Unit)
  }

  private def processIsolated(file: String, environment: Environment, uuid: String): Future[Unit] = {
    changeState(ProcessingFile(file))
    stagingArea.dequeue(Set(file))
    parse(file, environment, uuid)
      .map { bu =>
        repository.update(bu.baseUnit, resolve(bu.baseUnit))
        dependencies.foreach(
          _.onNewAst(BaseUnitListenerParams(bu, Map(), getResolvedUnit(bu.baseUnit.identifier)), uuid))
      }
  }

  def getResolvedUnit(uri: String)(): Future[AmfResolvedUnit] = {
    repository.getResolved(uri) match {
      case Some(f) => Future.successful(f)
      case _ =>
        getCompilableUnit(uri)
          .map(
            _ =>
              repository
                .getResolved(uri)
                .getOrElse(throw new Exception(s"Asked for an unknown resolved unit $uri")))
    }
  }

  def shutdown(): Future[Unit] = {
    changedFile(folder, WORKSPACE_TERMINATED)
    isDisabled.future
  }

  private def cleanFiles(closedFiles: List[(String, NotificationKind)]): Unit =
    closedFiles.foreach { cf =>
      repository.removeIsolated(cf._1)
      dependencies.foreach(_.onRemoveFile(cf._1))
    }

  private def processChangeConfigChanges(snapshot: Snapshot): Future[Unit] = {
    changeState(ProcessingProject)
    stagingArea.enqueue(snapshot.files.filterNot(t => t._2 == CHANGE_CONFIG))
    workspaceConfigurationProvider match {
      case Some(cp) =>
        cp.obtainConfiguration(environmentProvider.platform, snapshot.environment)
          .flatMap(processChangeConfig)
      case _ => Future.failed(new Exception("Expected Configuration Provider"))
    }
  }

  private def processChangeConfig(maybeConfig: Option[WorkspaceConf]): Future[Unit] = {
    configMainFile = maybeConfig
    maybeConfig match {
      case Some(conf) =>
        repository.setCachables(conf.cachables)
        processMFChanges(conf.mainFile, stagingArea.snapshot())
      case _ =>
        repository.cleanTree()
        repository.setCachables(Set.empty)
        Future.unit
    }
  }

  private def processMFChanges(mainFile: String, snapshot: Snapshot): Future[Unit] = {
    changeState(ProcessingProject)
    val uuid = UUID.randomUUID().toString
    parse(s"$folder/$mainFile", snapshot.environment, uuid)
      .flatMap { u =>
        repository.newTree(u, resolve(u.baseUnit)).map { _ =>
          dependencies.foreach(
            _.onNewAst(BaseUnitListenerParams(u, repository.references, getResolvedUnit(u.baseUnit.identifier)), uuid))
          stagingArea.enqueue(snapshot.files.filter(t => !repository.inTree(t._1)))
        }
      }
  }

  private def parse(uri: String, environment: Environment, uuid: String): Future[AmfParseResult] = {
    telemetryProvider.addTimedMessage("Start AMF Parse",
                                      "WorkspaceContentManager",
                                      "parse",
                                      MessageTypes.BEGIN_PARSE,
                                      uri,
                                      uuid)
    val eventualUnit = environmentProvider.amfConfiguration.parserHelper
      .parse(FileUtils.getDecodedUri(uri, environmentProvider.platform),
             environment.withResolver(repository.resolverCache))
    eventualUnit.foreach(
      _ =>
        telemetryProvider
          .addTimedMessage("End AMF Parse", "WorkspaceContentManager", "parse", MessageTypes.END_PARSE, uri, uuid))
    eventualUnit
  }

  def getRelationships(uri: String): Relationships =
    Relationships(repository, () => Some(getCompilableUnit(uri)))

  private def resolve(unit: BaseUnit): AmfResolvedUnitImpl =
    new AmfResolvedUnitImpl(unit)

  private class AmfResolvedUnitImpl(override val originalUnit: BaseUnit) extends AmfResolvedUnit {
    private val uri: String = originalUnit.identifier

    override protected def nextIfNotLast(): Option[Future[AmfResolvedUnit]] =
      repository
        .getParsed(uri)
        .flatMap { nextIfNotLatest(uri) }

    private def nextIfNotLatest(uri: String)(p: ParsedUnit): Option[Future[AmfResolvedUnit]] = {
      val unit = getCurrentCU(uri, p)
      if (latestYet(unit)) None
      else // get me the latest and check again
        Some(unit.getLast.map { _ =>
          repository.getResolved(uri).get
        })
    }

    private def latestYet(unit: CompilableUnit): Boolean =
      !unit.isDirty && unit.next.isEmpty && unit.unit.eq(originalUnit)

    override protected def resolvedUnitFn(): Future[BaseUnit] = {
      val uuid = UUID.randomUUID().toString
      telemetryProvider.addTimedMessage("resolve", MessageTypes.BEGIN_RESOLUTION, "begin resolution", uri, uuid)
      Future(
        environmentProvider.amfConfiguration.parserHelper
          .editingResolve(originalUnit.cloneUnit(), eh)) andThen {
        case _ =>
          telemetryProvider.addTimedMessage("resolve", MessageTypes.END_RESOLUTION, "end resolution", uri, uuid)
      }
    }
  }
}
