package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.core.client.scala.model.document.ExternalFragment
import amf.core.internal.remote.Platform
import amf.core.internal.validation.core.ValidationProfile
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.configuration.ConfigurationStyle.COMMAND
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.UnitTaskManager
import org.mulesoft.als.server.workspace.extract._
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

  private val hotReloadDialects = false

  def getConfigReader: Option[ConfigReader] = workspaceConfiguration.flatMap(_.configReader)

  def registeredDialects: Set[Dialect] = environmentProvider.amfConfiguration.dialects

  def getCurrentConfiguration: Future[Option[WorkspaceConfig]] =
    sync(
      () =>
        if (hasChangedConfigFile(stagingArea.snapshot()) || state == ProcessingProject) // may be changing
          current.flatMap(_ => getCurrentConfiguration)
        else
          Future.successful(workspaceConfiguration))

  private val rootHandler =
    new WorkspaceRootHandler(environmentProvider.amfConfigurationSnapshot(), projectConfigurationStyle)

  override def init(): Future[Unit] =
    rootHandler.extractConfiguration(folderUri, logger).flatMap { mainOption =>
      mainOption
        .map { conf =>
          logger.debug(s"folder: $folderUri", "WorkspaceContentManager", "init")
          logger.debug(s"main file: ${conf.mainFile}", "WorkspaceContentManager", "init")
          logger.debug(s"cachables: ${conf.cachables}", "WorkspaceContentManager", "init")
          workspaceConfiguration = Some(conf)
          withConfiguration( // why do we need configMainFile and also this configuration?
            DefaultWorkspaceConfigurationProvider(this,
                                                  conf.mainFile,
                                                  conf.cachables,
                                                  conf.profiles,
                                                  conf.semanticExtensions,
                                                  conf.dialects,
                                                  conf.configReader))
          super
            .init()
            .flatMap(_ => stage(conf.mainFile, CHANGE_CONFIG))

        }
        .getOrElse(super.init().map(_ => logger.debug(s"no main for $folderUri", "WorkspaceContentManager", "init")))
    }

  def containsFile(uri: String): Boolean =
    uri.startsWith(folderUri)

  def acceptsConfigUpdateByCommand: Boolean = projectConfigurationStyle.style == COMMAND

  implicit val platform: Platform = environmentProvider.platform // used for URI utils

  private val subscribers: Seq[BaseUnitListener] = allSubscribers.filter(_.isActive)

  private var workspaceConfiguration: Option[WorkspaceConfig] = None

  private def mainFile: Option[String] = workspaceConfiguration.map(_.mainFile)

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
      workspaceConfiguration
        .map(c => s"${c.rootFolder}/")
    else None

  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"

  def configFile: Option[String] =
    workspaceConfiguration.flatMap(ic => ic.configReader.map(cr => s"${ic.rootFolder}/${cr.configFileName}".toAmfUri))

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

  def isChanged(uri: String): Boolean = {
    val exists = for {
      memory     <- environmentProvider.filesInMemory.get(uri)
      lastParsed <- repository.getUnit(uri).flatMap(_.parsedResult.result.baseUnit.raw)
    } yield memory.text != lastParsed
    exists.getOrElse(true)
  }

  override protected def processTask(): Future[Unit] = {
    val snapshot: Snapshot    = stagingArea.snapshot()
    val (treeUnits, isolated) = snapshot.files.partition(u => isInMainTree(u._1.toAmfUri) || u._2 == CHANGE_CONFIG) // what if a new file is added between the partition and the override down
    logger.debug(s"units for main file: ${mainFile.getOrElse("[no main file]")}",
                 "WorkspaceContentManager",
                 "processTask")
    treeUnits.map(_._1).foreach(tu => logger.debug(s"tree unit: $tu", "WorkspaceContentManager", "processTask"))
    isolated.map(_._1).foreach(iu => logger.debug(s"isolated unit: $iu", "WorkspaceContentManager", "processTask"))
    val changedTreeUnits =
      treeUnits.filter(
        tu =>
          ((tu._2 == CHANGE_FILE ||
            tu._2 == OPEN_FILE) && isChanged(tu._1)) || // OPEN_FILE is used in case the IDE restarts and it reopens what was being edited
            tu._2 == CLOSE_FILE ||
            (tu._2 == FOCUS_FILE && shouldParseOnFocus(tu._1)))

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
    * - Workspace configuration has changed since las parse
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
          case _ => s.parsedResult.amfConfiguration.workspaceConfiguration != workspaceConfiguration
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
    workspaceConfiguration = maybeConfig

    def setCacheables(cacheables: Set[String]): Future[Unit] = {
      Future(repository.setCachables(cacheables.map(_.toAmfUri)))
    }

    def newTree(mf: Option[String]): Future[Unit] = {
      mf match {
        case Some(mainFile) if mainFile.nonEmpty => processMFChanges(mainFile, snapshot)
        case _                                   => Future(repository.cleanTree())
      }
    }

    for {
      _ <- setCacheables(maybeConfig.map(_.cachables).getOrElse(Set.empty))
      _ <- registerNewDialects(maybeConfig.map(c => c.semanticExtensions ++ c.dialects).getOrElse(Set.empty))
      r <- registerNewValidationProfiles(maybeConfig.map(_.profiles).getOrElse(Set.empty))
      _ <- newTree(maybeConfig.map(_.mainFile))
    } yield r
  }

  /**
    * Seeks new extensions in configuration, parses and registers
    */
  private def registerNewDialects(dialects: Set[String]): Future[Unit] = {
    val newDialects = dialects
      .diff(environmentProvider.amfConfiguration.dialects.map(_.identifier))
      .map(e => {
        if (e.isValidUri) e // full URI received
        else s"${trailSlash(folderUri)}$e" // if relative file from folder
      })
    newDialects.foreach(e =>
      logger.debug(s"Registering $e as dialect", "WorkspaceContentManager", "registerNewDialects"))
    Future
      .sequence(newDialects.map(parse(_, UUID.randomUUID().toString)))
      .map(_.map(_.result.baseUnit).map {
        case d: Dialect =>
          environmentProvider.amfConfiguration
            .registerDialect(d) // when properly implemented, check that this actually contains semantic extensions
        case b =>
          logger.error(s"The following dialect: ${b.identifier} is not valid",
                       "WorkspaceContentManager",
                       "registerNewDialects")
      })
  }

  private def registerNewValidationProfiles(validationProfiles: Set[String]): Future[Unit] = {
    environmentProvider.amfConfiguration.cleanValidationProfiles()
    Future
      .sequence(
        validationProfiles.map(parse(_, UUID.randomUUID().toString))
      )
      .map(_.flatMap(r => {
        if (r.result.baseUnit.isValidationProfile) {
          val d = r.result.baseUnit
          logger.debug("Adding validation profile: " + d.identifier,
                       "WorkspaceContentManager",
                       "registerNewValidationProfiles")
          environmentProvider.amfConfiguration.registerValidationProfile(d)
          d.location()
        } else {
          logger.error(s"The following validation profile: ${r.result.baseUnit.identifier} is not valid",
                       "WorkspaceContentManager",
                       "registerNewValidationProfiles")
          None
        }
      }))
      .flatMap(profiles => revalidateUnits(profiles))
  }

  private def revalidateUnits(validationProfiles: Set[String]): Future[Unit] = Future {
    val revalidateUris: List[String] = repository.getIsolatedUris.filter(uri => {
      repository
        .getUnit(uri)
        .flatMap(_.parsedResult.amfConfiguration.workspaceConfiguration.map(_.profiles))
        .getOrElse(Set.empty) != validationProfiles
    })
    if (revalidateUris.nonEmpty)
      revalidateUris.foreach(uri => {
        logger.debug(s"Enqueuing isolated file ($uri) because of changes on validation profiles",
                     "WorkspaceContentManager",
                     "processNewValidationProfiles")
        stagingArea.enqueue(uri, CHANGE_FILE)
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
      .withWorkspaceConfiguration(workspaceConfiguration)

    if (!hotReloadDialects) cacheConfig.useCache(repository.resolverCache)
    cacheConfig
      .parse(decodedUri)
      .map { r =>
        r.result.baseUnit match {
          case d: Dialect if hotReloadDialects =>
            logger.debug(s"registering as dialect uri: $decodedUri", "WorkspaceContentManager", "innerParse")
            environmentProvider.amfConfiguration.registerDialect(d)
            r
          case _ =>
            logger.debug(s"done with uri: $decodedUri", "WorkspaceContentManager", "innerParse")
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
