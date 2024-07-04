package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import amf.shapes.client.scala.model.document.DataTypeFragment
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.project.NewConfigurationListener
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.UnitTaskManager
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, AmfParseResult, ProjectConfigurationState}
import org.mulesoft.exceptions.AlsException
import org.mulesoft.lsp.feature.telemetry.MessageTypes

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceContentManager private (
    override val folderUri: String,
    environmentProvider: EnvironmentProvider,
    subscribers: () => List[WorkspaceContentListener[_]],
    override val repository: WorkspaceParserRepository,
    val projectConfigAdapter: ProjectConfigurationAdapter,
    hotReload: Boolean,
    maxFileSize: Option[Int],
    switchWorkspace: String => Unit,
    shouldRetryExternalFragments: Boolean
) extends UnitTaskManager[ParsedUnit, CompilableUnit, NotificationKind]
    with WorkspaceFolderManager
    with PlatformSecrets {

  def getConfigurationState: Future[ALSConfigurationState] =
    sync(() =>
      if (stagingArea.hasPending || state == ProcessingProject) // may be changing
        current.flatMap(_ => getConfigurationState)
      else
        current.flatMap(_ => projectConfigAdapter.getConfigurationState)
    )

  override def init(): Future[Unit] =
    Future {
      stagingArea.enqueue(folderUri, CHANGE_CONFIG)
    }.map(_ => {
      super
        .init()
        .map(_ =>
          Logger.debug(s"Finished initialization for workspace at '$folderUri'", "WorkspaceContentManager", "init")
        )
    })

  override def containsFile(uri: String): Future[Boolean] =
    projectConfigAdapter.getProjectConfiguration.map(_.containsInDependencies(uri) || uri.startsWith(folderUri))

  implicit val currentPlatform: Platform = this.platform

  private def baseUnitSubscribers: Seq[BaseUnitListener] =
    subscribers().collect({
      case buL: BaseUnitListener if buL.isActive => buL
    })

  private def configSubscribers: Seq[NewConfigurationListener] =
    subscribers().collect({
      case a: NewConfigurationListener if a.isActive => a
    })

  private def mainFile: Future[Option[String]] = projectConfigAdapter.mainFile

  def mainFileUri: Future[Option[String]] =
    initialized.flatMap(_ => current.flatMap(_ => mainFile.map(_.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri))))

  def getRootFolderFor(uri: String): Future[Option[String]] = {
    if (isInMainTree(uri)) {
      mainFileUri.flatMap({
        case Some(s) => Future(Some(stripToLastFolder(s)))
        case _       => getRootOf(uri)
      })
    } else Future.successful(None)
  }

  def isInMainTree(uri: String): Boolean =
    repository.inTree(uri)

  private def stripToLastFolder(uri: String): String =
    uri.substring(0, (uri.lastIndexOf('/') + 1).min(uri.length))

  private def getRootOf(uri: String): Future[Option[String]] =
    if (isInMainTree(uri)) {
      projectConfigAdapter.rootFolder
    } else Future.successful(None)

  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"

  override protected val stagingArea: ParserStagingArea = new ParserStagingArea(environmentProvider)

  override protected def toResult(uri: String, pu: ParsedUnit): CompilableUnit =
    pu.toCU(
      getNext(uri),
      pu.parsedResult.context.state.projectState.config.mainFile.map(mf => s"${trailSlash(folderUri)}$mf".toAmfUri),
      isDirty(uri),
      pu.parsedResult.context
    )

  private def isDirty(uri: String) =
    state == ProcessingProject ||
      (!isInMainTree(uri) && state != Idle) ||
      state == NotAvailable || stagingArea.hasPending

  def withConfiguration(configuration: ProjectConfiguration): Future[Unit] = {
    projectConfigAdapter.newProjectConfiguration(configuration)
    this.stage(configuration.folder, CHANGE_CONFIG)
  }

  def isChanged(uri: String): Boolean = {
    val exists = for {
      memory     <- environmentProvider.filesInMemory.get(uri)
      lastParsed <- repository.getUnit(uri).flatMap(_.parsedResult.result.baseUnit.raw)
    } yield memory.text != lastParsed
    exists.getOrElse(true)
  }

  override protected def processTask(): Future[Unit] = {
    val snapshot: Snapshot = stagingArea.snapshot()
    val (treeUnits, isolated) = snapshot.files.partition(u =>
      u._2 == CHANGE_CONFIG || isInMainTree(u._1.toAmfUri)
    ) // what if a new file is added between the partition and the override down
    treeUnits.map(_._1).foreach(tu => Logger.debug(s"Tree unit: $tu", "WorkspaceContentManager", "processTask"))
    isolated.map(_._1).foreach(iu => Logger.debug(s"Isolated unit: $iu", "WorkspaceContentManager", "processTask"))
    val future: Future[Future[Unit]] = for {
      mf                        <- mainFile
      currentConfigurationState <- projectConfigAdapter.getConfigurationState
    } yield {
      val changedTreeUnits =
        treeUnits.filter(tu =>
          ((tu._2 == CHANGE_FILE ||
            tu._2 == OPEN_FILE) && isChanged(
            tu._1
          )) || // OPEN_FILE is used in case the IDE restarts and it reopens what was being edited
            tu._2 == CLOSE_FILE ||
            (tu._2 == FOCUS_FILE && shouldParseOnFocus(tu._1, mf, currentConfigurationState))
        )
      Logger.debug(s"units for main file: ${mf.getOrElse("[no main file]")}", "WorkspaceContentManager", "processTask")
      if (hasChangedConfigFile(snapshot)) processChangeConfigChanges(snapshot)
      else if (changedTreeUnits.nonEmpty)
        processMFChanges(mf.get, snapshot).recoverWith { case e: Exception =>
          Logger.error(s"Error on parse: ${e.getMessage}", "WorkspaceContentManager", "processMFChanges")
          Future.unit
        }
      else
        processIsolatedChanges(isolated, currentConfigurationState)
    }
    future.flatten
  }

  private def hasChangedConfigFile(snapshot: Snapshot) =
    snapshot.files.map(_._2).contains(CHANGE_CONFIG)

  private def processIsolatedChanges(
      files: List[(String, NotificationKind)],
      currentConfiguration: ALSConfigurationState
  ): Future[Unit] = {
    val (closedFiles, changedFiles) = files.partition(_._2 == CLOSE_FILE)
    cleanFiles(closedFiles, currentConfiguration).flatMap(_ => {
      if (changedFiles.nonEmpty) {
        changeState(ProcessingFile)
        Future
          .sequence(changedFiles.map(t => processIsolated(t._1, UUID.randomUUID().toString)))
          .map(_ => Unit) // flatten the list to comply with signature
      } else Future.unit
    })
  }

  private def processIsolated(file: String, uuid: String): Future[Unit] =
    parse(file, asMain = false, uuid)
      .flatMap { result =>
        updateUnit(uuid, result, isDependency = false)
      }

  private def updateUnit(uuid: String, result: AmfParseResult, isDependency: Boolean): Future[Unit] = {
    repository.updateUnit(result)
    projectConfigAdapter.getConfigurationState.map(_ => {
      Logger.debug(s"Sending new AST from $folderUri", "WorkspaceContentManager", "processIsolated")
      baseUnitSubscribers.foreach(s =>
        try {
          s.onNewAst(BaseUnitListenerParams(result, Map.empty, tree = false, folderUri, isDependency), uuid)
        } catch {
          case e: Exception =>
            Logger.error(s"Subscriber $s threw ${e.getMessage}", "processIsolated", "WorkspaceContentManager")
        }
      )
    })
  }

  /** Called only for file that are part of the tree as isolated files are always parsed We should parse if:
    *   - Unit is dialect instance
    *   - Unit is external fragment and is the main file
    *   - Unit is external fragment and main file is external fragment too
    *   - Workspace configuration has changed since las parse
    *   - New dialect/extension registered
    */
  private def shouldParseOnFocus(
      uri: String,
      mainFileUri: Option[String],
      configurationState: ALSConfigurationState
  ): Boolean = {
    repository.getUnit(uri) match {
      case Some(s) =>
        s.parsedResult.result.baseUnit match {
          case _: DialectInstance => true
          case _: ExternalFragment
              if mainFileUri.exists(_.equals(uri)) ||
                mainFileUri.exists(u =>
                  repository.getUnit(u).exists(_.parsedResult.result.baseUnit.isInstanceOf[ExternalFragment])
                ) =>
            true
          case _ =>
            configurationState.projectState.config != s.parsedResult.context.state.projectState.config ||
            configurationState != s.parsedResult.context.state
        }
      case None => true
    }
  }

  override def shutdown(): Future[Unit] = {
    stage(folderUri, WORKSPACE_TERMINATED)
    super.shutdown()
  }

  private def cleanFiles(
      closedFiles: List[(String, NotificationKind)],
      currentConfiguration: ALSConfigurationState
  ): Future[Unit] = {
    closedFiles.foreach { cf =>
      repository.removeUnit(cf._1)
      baseUnitSubscribers.foreach(_.onRemoveFile(cf._1))

    }
    val p = currentConfiguration.projectState.config
    // restore registered profile/dialect/extension
    if (
      closedFiles
        .map(_._1)
        .exists(cf =>
          p.validationDependency.contains(cf) ||
            p.extensionDependency.contains(cf) ||
            p.metadataDependency.contains(cf)
        )
    )
      projectConfigAdapter.notifyUnits().map(_ => {})
    else Future.successful()
  }

  private def processChangeConfigChanges(snapshot: Snapshot): Future[Unit] = {
    changeState(ProcessingProject)
    Logger.debug(s"Processing Config Changes", "WorkspaceContentManager", "processChangeConfigChanges")
    stagingArea.enqueue(snapshot.files.filterNot(t => t._2 == CHANGE_CONFIG))
    (for {
      c        <- projectConfigAdapter.getConfigurationState
      mainFile <- Future(c.projectState.config.mainFile)
    } yield {
      processChangeConfig(c.projectState, snapshot, mainFile)
    }).flatten

  }

  private def processChangeConfig(
      config: ProjectConfigurationState,
      snapshot: Snapshot,
      mainFileUri: Option[String]
  ): Future[Unit] = {
    Logger.debug(
      s"\'MainFileUri\' received is: ${mainFileUri.getOrElse("Empty")}",
      "WorkspaceContentManager",
      "processChangeConfig"
    )
    val uuid = UUID.randomUUID().toString
    configSubscribers.foreach(_.onNewAst(config, uuid))
    (mainFileUri match {
      case Some(mainFile) if mainFile.nonEmpty => processMFChanges(mainFile, snapshot, uuid)
      case _                                   => Future(repository.cleanTree())
    }).map(_ => revalidateIsolatedUnits(config.profiles.map(_.path).toSet))
  }

  private def revalidateIsolatedUnits(validationProfiles: Set[String]): Future[Unit] = Future {
    def shouldValidate(maybeResult: Option[ParsedUnit]): Boolean = maybeResult.forall { result =>
      val changedProfiles =
        result.parsedResult.context.state.projectState.config.validationDependency != validationProfiles
      val isProfile = result.parsedResult.result.baseUnit.isValidationProfile
      changedProfiles && !isProfile
    }

    repository.getIsolatedUris
      .flatMap(uri => {
        if (shouldValidate(repository.getUnit(uri))) Some(uri)
        else None
      })
      .foreach(uri => {
        Logger.debug(
          s"Enqueuing isolated file ($uri) because of changes on validation profiles",
          "WorkspaceContentManager",
          "processNewValidationProfiles"
        )
        stage(uri, CHANGE_FILE)
      })
  }

  private def processMFChanges(
      mainFile: String,
      snapshot: Snapshot,
      uuid: String = UUID.randomUUID().toString
  ): Future[Unit] = {
    changeState(ProcessingProject)
    Logger.debug(
      s"Processing Tree changes with new \'mainFile\': $mainFile",
      "WorkspaceContentManager",
      "processMFChanges"
    )
    for {
      u <- parse(
        s"${if (folderUri != "" && !mainFile.contains(folderUri))
            trailSlash(folderUri)
          else folderUri}$mainFile",
        asMain = true,
        uuid
      )
      refs <- getTweakedRefs(u.result.baseUnit)
      _    <- repository.newTree(u, refs).flatMap(t => projectConfigAdapter.newTree(t))
    } yield {
      stagingArea.enqueue(snapshot.files.filterNot(_._2 == CHANGE_CONFIG).filter(t => !isInMainTree(t._1)))
      baseUnitSubscribers.foreach(s => {
        Logger.debug(
          s"Sending new AST from ${u.result.baseUnit.location().getOrElse(folderUri)}",
          "WorkspaceContentManager",
          "processMFChanges"
        )
        s.onNewAst(BaseUnitListenerParams(u, repository.references, tree = true, folderUri), uuid)
      })
    }
  }

  private def getTweakedRefs(baseUnit: BaseUnit): Future[Seq[BaseUnit]] =
    Logger.timeProcess(
      "AMF Parse - External Fragments",
      MessageTypes.BEGIN_PARSE_EXTERNAL,
      MessageTypes.END_PARSE_EXTERNAL,
      "WorkspaceContentManager : getTweakedRefs",
      folderUri,
      () =>
        Future.sequence(baseUnit.flatRefs.map {
          case bu @ (_: ExternalFragment | _: DataTypeFragment) if shouldRetryExternalFragments =>
            innerParse(bu.identifier, asMain = false)
              .map(r => r.result.baseUnit)
          case bu => Future.successful(bu)
        }),
      UUID.randomUUID().toString
    )

  private def parse(uri: String, asMain: Boolean, uuid: String): Future[AmfParseResult] = {
    Logger.timeProcess(
      "AMF Parse",
      MessageTypes.BEGIN_PARSE,
      MessageTypes.END_PARSE,
      "WorkspaceContentManager : parse",
      uri,
      innerParse(uri, asMain),
      uuid
    )
  }

  private def innerParse(uri: String, asMain: Boolean)(): Future[AmfParseResult] = {
    val decodedUri = uri.toAmfDecodedUri
    Logger.debug(s"Sent uri: $decodedUri", "WorkspaceContentManager", "innerParse")
    (for {
      state <- projectConfigAdapter.getConfigurationState
      r     <- state.parse(decodedUri, asMain, maxFileSize)
      newConfig <- projectConfigAdapter.getProjectConfiguration.map(config => {
        new ProjectConfiguration(
          config.folder,
          config.mainFile,
          config.designDependency,
          config.validationDependency,
          config.extensionDependency,
          config.metadataDependency + uri
        )
      })
    } yield {
      r.result.baseUnit match {
        case _: Dialect if hotReload =>
          Logger.debug(s"Hot registering as dialect uri: $decodedUri", "WorkspaceContentManager", "innerParse")
          withConfiguration(newConfig).map(_ => r)
        case _ =>
          Logger.debug(s"Done with uri: $decodedUri", "WorkspaceContentManager", "innerParse")
          Future(r)
      }
    }).flatten.recoverWith { case e: AlsException =>
      Logger.error(e.getMessage, "WorkspaceContentManager", "innerParse")
      repository.cleanTree()
      repository.getAllFilesUris.foreach(repository.removeUnit)
      switchWorkspace(folderUri)

      Future.failed(e)
    }
  }

  def getRelationships(uri: String): Future[Relationships] =
    getUnit(uri)
      .flatMap(_.getLast)
      .map(u => {
        Logger.debug(s"Getting relationships for ${u.uri}", "WorkspaceContentManager", "getRelationships")
        Relationships(repository, u)
      })

  override protected def log(msg: String, isError: Boolean = false): Unit =
    if (isError)
      Logger.error(msg, "WorkspaceContentManager", "Processing request")
    else Logger.debug(msg, "WorkspaceContentManager", "Processing request")

  override protected def disableTasks(): Future[Unit] = Future {
    baseUnitSubscribers.map(d => repository.getAllFilesUris.map(_.toAmfUri).foreach(d.onRemoveFile))
  }

}

object WorkspaceContentManager {
  def apply(
      folderUri: String,
      environmentProvider: EnvironmentProvider,
      subscribers: () => List[WorkspaceContentListener[_]],
      projectConfigAdapter: ProjectConfigurationAdapter,
      switchWorkspace: String => Unit,
      hotReload: Boolean = false,
      maxFileSize: Option[Int] = None,
      shouldRetryExternalFragments: Boolean = false
  ): Future[WorkspaceContentManager] = {
    val repository = new WorkspaceParserRepository()
    val wcm = new WorkspaceContentManager(
      folderUri,
      environmentProvider,
      subscribers,
      repository,
      projectConfigAdapter.withRepository(repository),
      hotReload,
      maxFileSize,
      switchWorkspace,
      shouldRetryExternalFragments
    )
    wcm.init()
    Future.successful(wcm)
  }
}
