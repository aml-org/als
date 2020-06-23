package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.UnitTaskManager
import org.mulesoft.als.server.workspace.extract.{WorkspaceConf, WorkspaceConfigurationProvider}
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceContentManager(val folder: String,
                              environmentProvider: EnvironmentProvider,
                              telemetryProvider: TelemetryProvider,
                              logger: Logger,
                              allSubscribers: List[BaseUnitListener])
    extends UnitTaskManager[ParsedUnit, CompilableUnit, NotificationKind] {

  implicit val platform: Platform = environmentProvider.platform // used for URI utils

  private val subscribers = allSubscribers.filter(_.isActive)

  private var configMainFile: Option[WorkspaceConf] = None

  def workspaceConfiguration: Option[WorkspaceConf] = configMainFile

  def setConfigMainFile(workspaceConf: Option[WorkspaceConf]): Unit = {
    repository.cleanTree()
    repository.setCachables(workspaceConf.map(_.cachables.map(_.toAmfUri)).getOrElse(Set.empty))
    configMainFile = workspaceConf
  }

  def mainFile: Option[String] = configMainFile.map(_.mainFile)
  def mainFileUri: Option[String] =
    mainFile.map(mf => s"${trailSlash(folder)}$mf".toAmfUri) // TODO: Analyze if we really need knowledge of both paths and uris (maybe set all to URI)

  def getRootFolderFor(uri: String): Option[String] =
    if (repository.inTree(uri))
      mainFileUri
        .map(stripToLastFolder)
        .orElse(getRootOf(uri))
    else None

  private def stripToLastFolder(uri: String): String =
    uri.substring(0, (uri.lastIndexOf('/') + 1).min(uri.length))

  private def getRootOf(uri: String): Option[String] =
    if (this.repository.inTree(uri))
      workspaceConfiguration
        .map(c => s"${c.rootFolder}/")
    else None

  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"

  def configFile: Option[String] =
    configMainFile.flatMap(ic => ic.configReader.map(cr => s"${ic.rootFolder}/${cr.configFileName}".toAmfUri))

  override protected val stagingArea: ParserStagingArea = new ParserStagingArea(environmentProvider)

  def environment: Environment                                                       = stagingArea.snapshot().environment
  override protected val repository                                                  = new WorkspaceParserRepository(logger)
  private var workspaceConfigurationProvider: Option[WorkspaceConfigurationProvider] = None

  def getCompilableUnit(uri: String): Future[CompilableUnit] = {
    val encodedUri = uri.toAmfUri
    repository.getUnit(encodedUri) match {
      case Some(pu) =>
        Future.successful(toResult(encodedUri, pu))
      case _ => getNext(encodedUri).getOrElse(fail(encodedUri))
    }
  }

  override protected def toResult(uri: String, pu: ParsedUnit): CompilableUnit =
    pu.toCU(getNext(uri), mainFileUri, repository.getReferenceStack(uri), state == NotAvailable)

  def withConfiguration(confProvider: WorkspaceConfigurationProvider): WorkspaceContentManager = {
    workspaceConfigurationProvider = Some(confProvider)
    this
  }

  override protected def processTask(): Future[Unit] = {
    val snapshot: Snapshot    = stagingArea.snapshot()
    val (treeUnits, isolated) = snapshot.files.partition(u => repository.inTree(u._1.toAmfUri)) // what if a new file is added between the partition and the override down
    val changedTreeUnits =
      treeUnits.filter(tu => tu._2 == CHANGE_FILE || tu._2 == CLOSE_FILE)

    if (hasChangedConfigFile(snapshot)) processChangeConfigChanges(snapshot)
    else if (changedTreeUnits.nonEmpty)
      processMFChanges(mainFile.get, snapshot) // it should not be possible for mainFile to be None if it gets here
    else
      processIsolatedChanges(isolated, snapshot.environment)
  }

  private def hasChangedConfigFile(snapshot: Snapshot) =
    snapshot.files.map(_._2).contains(CHANGE_CONFIG)

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
        repository.updateUnit(bu.baseUnit)
        subscribers.foreach(_.onNewAst(BaseUnitListenerParams(bu, Map.empty, tree = false), uuid))
      }
  }

  override def shutdown(): Future[Unit] = {
    stage(folder, WORKSPACE_TERMINATED)
    super.shutdown()
  }

  private def cleanFiles(closedFiles: List[(String, NotificationKind)]): Unit =
    closedFiles.foreach { cf =>
      repository.removeUnit(cf._1)
      subscribers.foreach(_.onRemoveFile(cf._1))
    }

  private def processChangeConfigChanges(snapshot: Snapshot): Future[Unit] = {
    changeState(ProcessingProject)
    stagingArea.enqueue(snapshot.files.filterNot(t => t._2 == CHANGE_CONFIG))
    workspaceConfigurationProvider match {
      case Some(cp) =>
        cp.obtainConfiguration(environmentProvider.platform, snapshot.environment, logger)
          .flatMap(processChangeConfig)
      case _ => Future.failed(new Exception("Expected Configuration Provider"))
    }
  }

  private def processChangeConfig(maybeConfig: Option[WorkspaceConf]): Future[Unit] = {
    configMainFile = maybeConfig
    maybeConfig match {
      case Some(conf) =>
        repository.setCachables(conf.cachables.map(_.toAmfUri))
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
        repository.newTree(u).map { _ =>
          subscribers.foreach(_.onNewAst(BaseUnitListenerParams(u, repository.references, tree = true), uuid))
          stagingArea.enqueue(snapshot.files.filter(t => !repository.inTree(t._1)))
        }
      }
  }

  private def parse(uri: String, environment: Environment, uuid: String): Future[AmfParseResult] = {
    telemetryProvider.timeProcess("AMF Parse",
                                  MessageTypes.BEGIN_PARSE,
                                  MessageTypes.END_PARSE,
                                  "WorkspaceContentManager : parse",
                                  uri,
                                  innerParse(uri, environment),
                                  uuid)
  }

  private def innerParse(uri: String, environment: Environment)() =
    environmentProvider.amfConfiguration.parserHelper
      .parse(uri.toAmfDecodedUri, environment.withResolver(repository.resolverCache))

  def getRelationships(uri: String): Relationships =
    Relationships(repository, () => Some(getCompilableUnit(uri.toAmfUri)))

  override protected def log(msg: String): Unit =
    logger.error(msg, "WorkspaceContentManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future {
    subscribers.map(d => repository.getAllFilesUris.map(_.toAmfUri).foreach(d.onRemoveFile))
  }
}
