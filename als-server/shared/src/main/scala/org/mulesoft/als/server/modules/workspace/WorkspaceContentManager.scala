package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.{WorkspaceConf, WorkspaceConfigurationProvider}
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
    stagingArea.enqueue(uri, kind)
    if (canProcess) current = process()
  }

  def getCompilableUnit(uri: String): Future[CompilableUnit] = {
    val encodedUri = FileUtils.getEncodedUri(uri, environmentProvider.platform)
    repository.getParsed(encodedUri) match {
      case Some(pu) =>
        Future.successful(pu.toCU(getNext(encodedUri), mainFile, repository.getReferenceStack(encodedUri)))
      case _ => getNext(encodedUri).getOrElse(fail(encodedUri))
    }
  }

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
    if (state == ShuttingDown) dependencies.foreach(_.closedWorkspace(repository.getPaths))
    if (stagingArea.isPending) next(processSnapshot())
    else goIdle()
  }

  def withConfiguration(confProvider: WorkspaceConfigurationProvider): WorkspaceContentManager = {
    workspaceConfigurationProvider = Some(confProvider)
    this
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
    state = Idle
    Future.unit
  }

  private def processIsolatedChanges(files: List[(String, NotificationKind)], environment: Environment): Future[Unit] = {
    val (closedFiles, changedFiles) = files.partition(_._2 == CLOSE_FILE)
    cleanFiles(closedFiles)

    if (changedFiles.nonEmpty)
      processIsolated(files.head._1, environment, UUID.randomUUID().toString)
    else Future.successful(Unit)
  }

  private def processIsolated(file: String, environment: Environment, uuid: String): Future[Unit] = {
    state = ProcessingFile(file)
    stagingArea.dequeue(Set(file))
    parse(file, environment, uuid)
      .map { bu =>
        repository.update(bu.baseUnit)
        dependencies.foreach(_.onNewAst((bu, Map()), uuid))
      }
  }

  def shutdown(): Unit = {
    if (state != Idle) current.onComplete(_ => {
      state = ShuttingDown
      process()
    })
    else state = ShuttingDown
    process()
  }

  private def cleanFiles(closedFiles: List[(String, NotificationKind)]): Unit =
    closedFiles.foreach(cf => dependencies.foreach(_.onRemoveFile(cf._1)))

  private def processChangeConfigChanges(snapshot: Snapshot): Future[Unit] = {
    state = ProcessingProject
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
    state = ProcessingProject
    val uuid = UUID.randomUUID().toString
    parse(s"$folder/$mainFile", snapshot.environment, uuid)
      .flatMap { u =>
        repository.newTree(u).map { _ =>
          dependencies.foreach(_.onNewAst((u, repository.references), uuid))
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
}
