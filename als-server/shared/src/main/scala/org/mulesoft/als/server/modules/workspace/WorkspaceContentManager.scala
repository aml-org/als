package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.core.client.scala.model.document.ExternalFragment
import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.configuration.ConfigurationStyle.COMMAND
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.UnitTaskManager
import org.mulesoft.als.server.workspace.extract.{
  ConfigReader,
  DefaultWorkspaceConfigurationProvider,
  WorkspaceConfig,
  WorkspaceConfigurationProvider,
  WorkspaceRootHandler
}
import org.mulesoft.amfintegration.AmfImplicits.{BaseUnitImp, DialectImplicits}
import org.mulesoft.amfintegration.amfconfiguration.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceContentManager private (val folderUri: String,
                                       environmentProvider: EnvironmentProvider,
                                       telemetryProvider: TelemetryProvider,
                                       logger: Logger,
                                       allSubscribers: List[BaseUnitListener],
                                       projectConfigurationStyle: ProjectConfigurationStyle)
    extends UnitTaskManager[ParsedUnit, CompilableUnit, NotificationKind] {
  def getConfigReader: Option[ConfigReader] = configMainFile.flatMap(_.configReader)

  def registeredDialects: Set[Dialect] = environmentProvider.amfConfiguration.dialects

  def getCurrentConfiguration: Future[Option[WorkspaceConfig]] =
    sync(
      () =>
        if (hasChangedConfigFile(stagingArea.snapshot()) || state == ProcessingProject) // may be changing
          current.flatMap(_ => getCurrentConfiguration)
        else
          Future.successful(configMainFile))

  private val rootHandler =
    new WorkspaceRootHandler(environmentProvider.amfConfigurationSnapshot(), projectConfigurationStyle)

  override def init(): Future[Unit] =
    rootHandler.extractConfiguration(folderUri, logger).flatMap { mainOption =>
      mainOption
        .map { conf =>
          logger.debug(s"folder: $folderUri", "WorkspaceContentManager", "init")
          logger.debug(s"main file: ${conf.mainFile}", "WorkspaceContentManager", "init")
          logger.debug(s"cachables: ${conf.cachables}", "WorkspaceContentManager", "init")
          configMainFile = Some(conf)
          withConfiguration( // why do we need configMainFile and also this configuration?
            DefaultWorkspaceConfigurationProvider(this,
                                                  conf.mainFile,
                                                  conf.cachables,
                                                  conf.profiles,
                                                  conf.semanticExtensions,
                                                  conf.configReader))
          super
            .init()
            .flatMap(_ => stage(conf.mainFile, CHANGE_CONFIG))

        }
        .getOrElse(super.init().map(_ => logger.debug(s"no main for $folderUri", "WorkspaceContentManager", "init")))
    }

  def containsFile(uri: String): Boolean = {
    logger.debug(s"checking if $uri corresponds to $folderUri", "WorkspaceContentManager", "containsFile")
    uri.startsWith(folderUri)
  }

  def acceptsConfigUpdateByCommand: Boolean = projectConfigurationStyle.style == COMMAND

  implicit val platform: Platform = environmentProvider.platform // used for URI utils

  private val subscribers: Seq[BaseUnitListener] = allSubscribers.filter(_.isActive)

  private var configMainFile: Option[WorkspaceConfig] = None

  private def mainFile: Option[String] = configMainFile.map(_.mainFile)

  def mainFileUri: Future[Option[String]] =
    initialized.map(_ => mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri))

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
      configMainFile
        .map(c => s"${c.rootFolder}/")
    else None

  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"

  def configFile: Option[String] =
    configMainFile.flatMap(ic => ic.configReader.map(cr => s"${ic.rootFolder}/${cr.configFileName}".toAmfUri))

  override protected val stagingArea: ParserStagingArea = new ParserStagingArea(environmentProvider, logger)

  override protected val repository = new WorkspaceParserRepository(environmentProvider.amfConfiguration, logger)

  private var workspaceConfigurationProvider: Option[WorkspaceConfigurationProvider] = None

  override protected def toResult(uri: String, pu: ParsedUnit): CompilableUnit =
    pu.toCU(
      getNext(uri),
      mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri),
      repository.getReferenceStack(uri),
      isDirty(uri),
      pu.parsedResult.amfConfiguration // environmentProvider.amfConfigurationSnapshot()
    )

  private def isDirty(uri: String) =
    state == ProcessingProject ||
      (!isInMainTree(uri) && state != Idle) ||
      state == NotAvailable || stagingArea.hasPending

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
      processIsolatedChanges(isolated)
  }

  private def hasChangedConfigFile(snapshot: Snapshot) =
    snapshot.files.map(_._2).contains(CHANGE_CONFIG)

  private def processIsolatedChanges(files: List[(String, NotificationKind)]): Future[Unit] = {
    val (closedFiles, changedFiles) = files.partition(_._2 == CLOSE_FILE)
    cleanFiles(closedFiles)

    if (changedFiles.nonEmpty) {
      changeState(ProcessingFile)
      Future
        .sequence(changedFiles.map(t => processIsolated(t._1, UUID.randomUUID().toString)))
        .map(_ => Unit) //flatten the list to comply with signature
    } else Future.unit
  }

  private def processIsolated(file: String, uuid: String): Future[Unit] =
    parse(file, uuid)
      .map { bu =>
        repository.updateUnit(bu)
        logger.debug(s"sending new AST from $folderUri", "WorkspaceContentManager", "processIsolated")

        subscribers.foreach(s =>
          try {
            s.onNewAst(BaseUnitListenerParams(bu, Map.empty, tree = false), uuid)
          } catch {
            case e: Exception =>
              logger.error(s"subscriber $s threw ${e.getMessage}", "processIsolated", "WorkspaceContentManager")
        })
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
        s.parsedResult.result.baseUnit match {
          case _: DialectInstance => true
          case _: ExternalFragment
              if mainFileUri.exists(_.equals(uri)) ||
                mainFileUri.exists(
                  u => repository.getUnit(u).exists(_.parsedResult.result.baseUnit.isInstanceOf[ExternalFragment])) =>
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
    logger.debug(s"Processing Config Changes", "WorkspaceContentManager", "processChangeConfigChanges")
    stagingArea.enqueue(snapshot.files.filterNot(t => t._2 == CHANGE_CONFIG))
    workspaceConfigurationProvider match {
      case Some(cp) =>
        cp.obtainConfiguration(environmentProvider.amfConfiguration, logger)
          .flatMap(processChangeConfig(_, snapshot))
      case _ => Future.failed(new Exception("Expected Configuration Provider"))
    }
  }

  private def processChangeConfig(maybeConfig: Option[WorkspaceConfig], snapshot: Snapshot): Future[Unit] = {
    configMainFile = maybeConfig
    maybeConfig match {
      case Some(conf) =>
        repository.setCachables(conf.cachables.map(_.toAmfUri))
        registerNewExtensions(conf)
          .flatMap { _ =>
            if (conf.mainFile != "") processMFChanges(conf.mainFile, snapshot) else Future(repository.cleanTree())
          }
      case _ =>
        repository.cleanTree()
        repository.setCachables(Set.empty)
        Future.unit
    }
  }

  /**
    * Seeks new extensions in configuration, parses and registers
    */
  private def registerNewExtensions(conf: WorkspaceConfig): Future[Unit] = {
    val newExtensions = conf.semanticExtensions
      .diff(environmentProvider.amfConfiguration.dialects.map(_.identifier))
      .map(e => {
        if (e.isValidUri) e // full URI received
        else s"${trailSlash(folderUri)}$e" // if relative file from folder
      })
    newExtensions.foreach(e =>
      logger.debug(s"Registering $e as extension", "WorkspaceContentManager", "registerNewExtensions"))
    Future
      .sequence(newExtensions.map(parse(_, UUID.randomUUID().toString)))
      .map(_.map(_.result.baseUnit).foreach {
        case d: Dialect =>
          environmentProvider.amfConfiguration
            .registerDialect(d) // when properly implemented, check that this actually contains semantic extensions
        case b =>
          logger.error(s"The following extension: ${b.identifier} is not valid",
                       "WorkspaceContentManager",
                       "registerNewExtensions")
      })
  }

  private def processMFChanges(mainFile: String, snapshot: Snapshot): Future[Unit] = {
    changeState(ProcessingProject)
    logger.debug(s"Processing Tree changes", "WorkspaceContentManager", "processMFChanges")
    val uuid = UUID.randomUUID().toString
    parse(s"${trailSlash(folderUri)}$mainFile", uuid)
      .flatMap { u =>
        repository.newTree(u).map { _ =>
          stagingArea.enqueue(snapshot.files.filterNot(_._2 == CHANGE_CONFIG).filter(t => !isInMainTree(t._1)))
          subscribers.foreach(s => {
            logger.debug(s"sending new AST from ${u.result.baseUnit.location().getOrElse(folderUri)}",
                         "WorkspaceContentManager",
                         "processMFChanges")
            s.onNewAst(BaseUnitListenerParams(u, repository.references, tree = true), uuid)
          })
        }
      }
      .recoverWith {
        case e: Exception =>
          logger.error(s"Error on parse: ${e.getMessage}", "WorkspaceContentManager", "processMFChanges")
          Future.unit
      }
  }

  private def parse(uri: String, uuid: String): Future[AmfParseResult] = {
    telemetryProvider.timeProcess("AMF Parse",
                                  MessageTypes.BEGIN_PARSE,
                                  MessageTypes.END_PARSE,
                                  "WorkspaceContentManager : parse",
                                  uri,
                                  innerParse(uri),
                                  uuid)
  }

  private def innerParse(uri: String)(): Future[AmfParseResult] = {
    val decodedUri = uri.toAmfDecodedUri
    logger.debug(s"sent uri: $decodedUri", "WorkspaceContentManager", "innerParse")
    val cacheConfig = environmentProvider
      .amfConfigurationSnapshot()
      .withWorkspaceConfiguration(configMainFile)
    cacheConfig.useCache(repository.resolverCache)
    cacheConfig
      .parse(decodedUri)
      .map { r =>
        // temporal registration until new registration is implemented, this should work as with AMF 4.x.x
        r.result.baseUnit match {
          case d: Dialect =>
            environmentProvider.amfConfiguration.registerDialect(d)
            r
          case _ =>
            r
        }
      }
  }

  def getRelationships(uri: String): Future[Relationships] =
    getUnit(uri)
      .flatMap(_.getLast)
      .map(u => {
        logger.debug(s"getting relationships for ${u.uri}", "WorkspaceContentManager", "getRelationships")
        Relationships(repository, u)
      })

  override protected def log(msg: String, isError: Boolean = false): Unit =
    if (isError)
      logger.error(msg, "WorkspaceContentManager", "Processing request")
    else logger.debug(msg, "WorkspaceContentManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future {
    subscribers.map(d => repository.getAllFilesUris.map(_.toAmfUri).foreach(d.onRemoveFile))
  }
}

object WorkspaceContentManager {
  def apply(folderUri: String,
            environmentProvider: EnvironmentProvider,
            telemetryProvider: TelemetryProvider,
            logger: Logger,
            allSubscribers: List[BaseUnitListener],
            projectConfigurationStyle: ProjectConfigurationStyle): Future[WorkspaceContentManager] = {
    val wcm = new WorkspaceContentManager(folderUri,
                                          environmentProvider,
                                          telemetryProvider,
                                          logger,
                                          allSubscribers,
                                          projectConfigurationStyle)
    wcm.init().map(_ => wcm)
  }
}
