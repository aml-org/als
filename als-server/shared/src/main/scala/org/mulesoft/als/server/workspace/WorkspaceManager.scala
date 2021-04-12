package org.mulesoft.als.server.workspace

import amf.core.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.AlsWorkspaceService
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.als.server.workspace.extract._
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, ExecuteCommandParams}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider,
                       telemetryProvider: TelemetryProvider,
                       val allSubscribers: List[BaseUnitListener],
                       override val dependencies: List[AccessUnits[CompilableUnit]],
                       logger: Logger)
    extends TextListener
    with UnitWorkspaceManager
    with UnitsManager[CompilableUnit, BaseUnitListenerParams]
    with AlsWorkspaceService {

  implicit val platform: Platform                  = environmentProvider.platform
  override def subscribers: List[BaseUnitListener] = allSubscribers.filter(_.isActive)
  private val rootHandler =
    new WorkspaceRootHandler(environmentProvider.platform, environmentProvider.environmentSnapshot())
  private val workspaces: SynchronizedList[WorkspaceContentManager] = new SynchronizedList()

  private val lock = new FutureLock()

  def getWorkspace(uri: String): Future[WorkspaceContentManager] = {
    if (!lock.isBlocked) Future {
      findWorkspace(uri)
    } else {
      getWorkspace(uri)
    }
  }

  private def findWorkspace(uri: String): WorkspaceContentManager =
    workspaces.find(ws => ws.containsFile(uri)).getOrElse(defaultWorkspace)

  def initializeWS(root: String): Future[Unit] = {
    val workspace: WorkspaceContentManager =
      new WorkspaceContentManager(root, environmentProvider, telemetryProvider, logger, subscribers)
    removeWorkspacesWithRoot(root).map(_ => {
      logger
        .debug("Adding workspace: " + root, "WorkspaceManager", "initializeWS")
      workspaces += workspace
    })

    // should we?
    cleanDisabledWorkspaces()

    rootHandler.extractConfiguration(root, logger).flatMap { mainOption =>
      Future {
        workspace.setConfigMainFile(mainOption)
        mainOption.foreach(
          conf =>
            // Already locked here, so we can call changeContentManagerConfiguration directly
            changeContentManagerConfiguration(workspace,
                                              conf.mainFile,
                                              conf.cachables,
                                              mainOption.flatMap(_.configReader)))
      }
    }
  }

  private def cleanDisabledWorkspaces(): Unit =
    workspaces.filter(_.isTerminated).foreach(w => workspaces -= w)

  private def removeWorkspacesWithRoot(root: String): Future[Seq[Unit]] = {
    Future.sequence(
      workspaces
        .filter(ws => ws.folder.startsWith(root))
        .map(w => {
          // We remove every workspace that is a subdirectory of the one being added
          logger.debug("Replacing Workspace: " + w.folder + " due to " + root, "WorkspaceManager", "initializeWS")
          shutdownWS(w)
        }))
  }

  def shutdownWS(workspace: WorkspaceContentManager): Future[Unit] = {
    logger.debug("Removing workspace: " + workspace.folder, "WorkspaceManager", "shutdownWS")
    workspace.shutdown().map(_ => workspaces -= workspace)
  }

  override def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri.toAmfUri).flatMap(_.getUnit(uri.toAmfUri))

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  def getLastUnit(workspace: WorkspaceContentManager, uri: String, uuid: String): Future[CompilableUnit] =
    workspace.getUnit(uri.toAmfUri).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = findWorkspace(uri.toAmfUri)
    logger.debug(s"Notify $uri", "WorkspaceManager", "blocking")
    if (manager.configFile
          .map(_.toAmfUri)
          .contains(uri.toAmfUri)) {
      manager.withConfiguration(ReaderWorkspaceConfigurationProvider(manager))
      manager.stage(uri.toAmfUri, CHANGE_CONFIG)
    } else manager.stage(uri.toAmfUri, kind)
  }

  private def blockingWorkspaces[T](func: () => Future[T]): Future[Unit] = {
    logger.debug("Blocking workspaces", "WorkspaceManager", "blockingWorkspaces")
    val workspaces = (this.workspaces :+ defaultWorkspace).filterNot(_.isTerminated)
    workspaces.foreach(wcm => wcm.stage(wcm.folder, BLOCK_WORKSPACE))
    logger.debug("Blocking workspaces - notification sent", "WorkspaceManager", "blockingWorkspaces")
    def blockedWorkspaces(fun: () => Future[Unit]): Future[Unit] = {
      Future
        .sequence(workspaces.map(_.future))
        .flatMap(_ =>
          if (workspaces.forall(_.isBlocked)) {
            logger.debug("Workspaces blocked", "WorkspaceManager", "blockingWorkspaces")
            fun()
          } else {
            logger.debug("Workspaces not blocked, retrying", "WorkspaceManager", "blockingWorkspaces")
            blockedWorkspaces(fun)
        })
    }

    blockedWorkspaces(() => {
      func().map(_ => {
        logger.debug("Unblocking workspaces", "WorkspaceManager", "blockingWorkspaces")
        workspaces.foreach(wcm => wcm.stage(wcm.folder, UNBLOCK_WORKSPACE))
      })
    })
  }

  private def blocking[T](fn: () => Future[T]) = {
    for {
      _ <- lock.block()
      r <- fn()
    } yield {
      lock.release()
      r
    }
  }

  def contentManagerConfiguration(manager: WorkspaceContentManager,
                                  mainSubUri: String,
                                  dependencies: Set[String],
                                  reader: Option[ConfigReader]): Future[Unit] =
    blocking(() =>
      Future {
        changeContentManagerConfiguration(manager, mainSubUri, dependencies, reader)
    })

  /**
    * Changes the configuration of a workspace. Calling this function requires you to have locked the WorkspaceManager
    * or else there could be sync errors
    */
  private def changeContentManagerConfiguration(manager: WorkspaceContentManager,
                                                mainSubUri: String,
                                                dependencies: Set[String],
                                                reader: Option[ConfigReader]): Unit =
    manager
      .withConfiguration(DefaultWorkspaceConfigurationProvider(manager, mainSubUri, dependencies, reader))
      .stage(mainSubUri, CHANGE_CONFIG)

  override def executeCommand(params: ExecuteCommandParams): Future[AnyRef] =
    commandExecutors.get(params.command) match {
      case Some(exe) =>
        exe.runCommand(params)
      case _ =>
        logger.error(s"Command [${params.command}] not recognized", "WorkspaceManager", "executeCommand")
        Future.successful(Unit) // future failed?
    }

  private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
    Commands.DID_FOCUS_CHANGE_COMMAND -> new DidFocusCommandExecutor(logger, this),
    Commands.DID_CHANGE_CONFIGURATION -> new DidChangeConfigurationCommandExecutor(logger, this),
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(logger, environmentProvider.amfConfiguration)
  )

  val defaultWorkspace =
    new WorkspaceContentManager("", environmentProvider, telemetryProvider, logger, subscribers)

  override def getProjectRootOf(uri: String): Future[Option[String]] =
    getWorkspace(uri).map(_.getRootFolderFor(uri))

  override def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit] =
    // Drop all old workspaces
    blocking(() => {
      workspaces.clear()
      val newWorkspaces = extractCleanURIs(workspaceFolders)
      dependencies.foreach(d => d.withUnitAccessor(this))
      blockingWorkspaces(() => {
        Future.sequence(newWorkspaces.map(initializeWS))
      })
    })

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Unit = {
    val event          = params.event
    val deletedFolders = event.deleted.flatMap(_.uri)

    workspaces
      .filter(p => deletedFolders.contains(p.folder))
      .foreach(shutdownWS)
    blocking(() => {
      blockingWorkspaces(() => {
        Future.sequence(event.added.flatMap(_.uri).map(initializeWS))
      })
    })
  }

  dependencies.foreach(d => d.withUnitAccessor(this))

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    getLastUnit(uri.toAmfUri, uuid).flatMap(_ =>
      getWorkspace(uri.toAmfUri).flatMap(_.getRelationships(uri.toAmfUri).getDocumentLinks(uri.toAmfUri)))

  override def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]] =
    getWorkspace(uri)
      .flatMap(workspace => {
        workspace.mainFileUri match {
          case Some(mf) =>
            getLastUnit(workspace, mf, uuid)
              .flatMap(_ => workspace.getRelationships(mf).getAllDocumentLinks)
          case _ => Future.successful(Map.empty)
        }
      })

  override def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]] =
    getWorkspace(uri).flatMap(_.getRelationships(uri).getAliases(uri))

  private def filterDuplicates(links: Seq[RelationshipLink]): Seq[RelationshipLink] = {
    val res = mutable.ListBuffer[RelationshipLink]()
    links.foreach { l =>
      if (!res.exists(_.relationshipIsEqual(l)))
        res += l
    }
    res
  }

  // tepm until have class for all relationships from visitors result associated to CU.
  override def getRelationships(uri: String, uuid: String): Future[(CompilableUnit, Seq[RelationshipLink])] =
    getWorkspace(uri)
      .flatMap(workspace => {
        getLastUnit(workspace, uri, uuid).flatMap(
          cu =>
            workspace
              .getRelationships(uri)
              .getRelationships(uri)
              .map(rl => (cu, filterDuplicates(rl))))
      })

  override def isInMainTree(uri: String): Boolean =
    findWorkspace(uri).isInMainTree(uri)

  class SynchronizedList[T] {
    private val internal: ListBuffer[T] = ListBuffer()

    def filter(fn: T => Boolean): Seq[T] = internal.filter(fn)

    def +=(item: T): SynchronizedList[T] = {
      this.synchronized {
        internal += item
      }
      this
    }

    def -=(item: T): SynchronizedList[T] = {
      this.synchronized {
        internal -= item
      }
      this
    }

    def clear(): SynchronizedList[T] = {
      this.synchronized {
        internal.clear()
      }
      this
    }

    def :+(item: T): Seq[T] = internal :+ item

    def foreach[U](fn: T => U): Unit = internal.foreach(fn)

    def forall(fn: T => Boolean): Boolean = internal.forall(fn)

    def exists(fn: T => Boolean): Boolean = internal.exists(fn)

    def find(fn: T => Boolean): Option[T] = internal.find(fn)

  }
}
