package org.mulesoft.als.server.modules.workspace

import amf.client.model.document.DialectInstance
import amf.core.model.document.ExternalFragment

import java.util.UUID
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.UnitTaskManager
import org.mulesoft.als.server.workspace.extract.{
  DefaultWorkspaceConfigurationProvider,
  WorkspaceConf,
  WorkspaceConfigurationProvider,
  WorkspaceRootHandler
}
import org.mulesoft.amfintegration.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// todo: should have companion object which creates `new WorkspaceContentManager(...).init`
class WorkspaceContentManager(val folderUri: String,
                              environmentProvider: EnvironmentProvider,
                              telemetryProvider: TelemetryProvider,
                              logger: Logger,
                              allSubscribers: List[BaseUnitListener])
    extends UnitTaskManager[ParsedUnit, CompilableUnit, NotificationKind] {

  private val rootHandler =
    new WorkspaceRootHandler(environmentProvider.platform, environmentProvider.environmentSnapshot())

  // todo: should return a Future and be called from companion object for creation
  override def init(): Unit =
    rootHandler.extractConfiguration(folderUri, logger).map { mainOption =>
      if (mainOption.isEmpty) logger.debug(s"no main for $folderUri", "WorkspaceContentManager", "init")
      mainOption.foreach(
        conf => {
          logger.debug(s"folder: $folderUri", "WorkspaceContentManager", "init")
          logger.debug(s"main file: ${conf.mainFile}", "WorkspaceContentManager", "init")
          logger.debug(s"cachables: ${conf.cachables}", "WorkspaceContentManager", "init")
          this
            .withConfiguration(
              DefaultWorkspaceConfigurationProvider(this,
                                                    conf.mainFile,
                                                    conf.cachables,
                                                    mainOption.flatMap(_.configReader)))
            .stage(conf.mainFile, CHANGE_CONFIG)
          configMainFile = Some(conf)
        }
      )
      super.init()
    }

  def containsFile(uri: String): Boolean = {
    logger.debug(s"checking if $uri corresponds to $folderUri", "WorkspaceContentManager", "containsFile")
    uri.startsWith(folderUri)
  }

  implicit val platform: Platform = environmentProvider.platform // used for URI utils

  private val subscribers = allSubscribers.filter(_.isActive)

  private var configMainFile: Option[WorkspaceConf] = None

  def workspaceConfiguration: Option[WorkspaceConf] = configMainFile

  // not used?
  def setConfigMainFile(workspaceConf: Option[WorkspaceConf]): Unit = {
    repository.cleanTree()
    repository.setCachables(workspaceConf.map(_.cachables.map(_.toAmfUri)).getOrElse(Set.empty))
    configMainFile = workspaceConf
  }

  private def mainFile: Option[String] = configMainFile.map(_.mainFile)

  def mainFileUri: Future[Option[String]] =
    isInitialized.future.map(_ => mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri))

  def getRootFolderFor(uri: String): Future[Option[String]] =
    if (isInMainTree(uri))
      mainFileUri.map(
        _.map(stripToLastFolder)
          .orElse(getRootOf(uri)))
    else Future.successful(None)

  def isInMainTree(uri: String): Boolean =
    repository.inTree(uri)

  private def stripToLastFolder(uri: String): String =
    uri.substring(0, (uri.lastIndexOf('/') + 1).min(uri.length))

  private def getRootOf(uri: String): Option[String] =
    if (isInMainTree(uri))
      workspaceConfiguration
        .map(c => s"${c.rootFolder}/")
    else None

  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"

  def configFile: Option[String] =
    configMainFile.flatMap(ic => ic.configReader.map(cr => s"${ic.rootFolder}/${cr.configFileName}".toAmfUri))

  override protected val stagingArea: ParserStagingArea = new ParserStagingArea(environmentProvider, logger)

  override protected val repository = new WorkspaceParserRepository(logger)

  private var workspaceConfigurationProvider: Option[WorkspaceConfigurationProvider] = None

  override protected def toResult(uri: String, pu: ParsedUnit): CompilableUnit =
    pu.toCU(getNext(uri),
            mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri),
            repository.getReferenceStack(uri),
            isDirty(uri))

  private def isDirty(uri: String) =
    state == ProcessingProject ||
//    (!repository.inTree(uri) && (state == ProcessingFile(uri) || stagingArea.contains(uri))) ||
//  TODO: check if upper statement can replace the one underneath
//    (if a file is processing, does the rest stay on the staging area??
      (!isInMainTree(uri) && state != Idle) ||
      state == NotAvailable

  def withConfiguration(confProvider: WorkspaceConfigurationProvider): WorkspaceContentManager = {
    workspaceConfigurationProvider = Some(confProvider)
    this
  }

  override protected def processTask(): Future[Unit] = {
    val snapshot: Snapshot    = stagingArea.snapshot()
    val (treeUnits, isolated) = snapshot.files.partition(u => isInMainTree(u._1.toAmfUri)) // what if a new file is added between the partition and the override down
    logger.debug(s"units for main file: ${mainFile.getOrElse("[no main file]")}",
                 "WorkspaceContentManager",
                 "processTask")
    treeUnits.map(_._1).foreach(tu => logger.debug(s"tree unit: $tu", "WorkspaceContentManager", "processTask"))
    isolated.map(_._1).foreach(iu => logger.debug(s"isolated unit: $iu", "WorkspaceContentManager", "processTask"))
    val changedTreeUnits =
      treeUnits.filter(tu =>
        tu._2 == CHANGE_FILE || tu._2 == CLOSE_FILE || (tu._2 == FOCUS_FILE && shouldParseOnFocus(tu._1)))

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

    if (changedFiles.nonEmpty) {
      changeState(ProcessingFile)
      Future
        .sequence(changedFiles.map(t => processIsolated(t._1, environment, UUID.randomUUID().toString)))
        .map(r => Unit) //flatten the list to comply with signature
    } else Future.unit
  }

  private def processIsolated(file: String, environment: Environment, uuid: String): Future[Unit] =
    parse(file, environment, uuid)
      .map { bu =>
        repository.updateUnit(bu)
        subscribers.foreach(_.onNewAst(BaseUnitListenerParams(bu, Map.empty, tree = false), uuid))
      }

  /**
    * Called only for file that are part of the tree as isolated files are always parsed
    * We should parse if:
    * - Unit is dialect instance
    * - Unit is external fragment and is the main file
    * - Unit is external fragment and main file is external fragment too
    */
  private def shouldParseOnFocus(uri: String): Boolean = {
    val mainFileUri = mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri)
    repository.getUnit(uri) match {
      case Some(s) =>
        s.bu match {
          case _: DialectInstance => true
          case _: ExternalFragment
              if mainFileUri.exists(_.equals(uri)) ||
                mainFileUri.exists(u => repository.getUnit(u).exists(_.bu.isInstanceOf[ExternalFragment])) =>
            true
          case _ => false
        }
      case None => true
    }
  }

  override def shutdown(): Future[Unit] = {
    stage(folderUri, WORKSPACE_TERMINATED)
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
    parse(s"$folderUri/$mainFile", snapshot.environment, uuid)
      .flatMap { u =>
        repository.newTree(u).map { _ =>
          subscribers.foreach(_.onNewAst(BaseUnitListenerParams(u, repository.references, tree = true), uuid))
          stagingArea.enqueue(snapshot.files.filter(t => !isInMainTree(t._1)))
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

  private def innerParse(uri: String, environment: Environment)(): Future[AmfParseResult] = {
    val decodedUri = uri.toAmfDecodedUri
    logger.debug(s"sent uri: $decodedUri", "WorkspaceContentManager", "innerParse")
    environmentProvider.amfConfiguration
      .modelBuilder()
      .parse(decodedUri, environment.withResolver(repository.resolverCache))
  }

  def getRelationships(uri: String): Relationships =
    Relationships(repository, () => Some(getUnit(uri)))

  override protected def log(msg: String): Unit =
    logger.error(msg, "WorkspaceContentManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future {
    subscribers.map(d => repository.getAllFilesUris.map(_.toAmfUri).foreach(d.onRemoveFile))
  }

  // Initialize after construction
  init()
}
