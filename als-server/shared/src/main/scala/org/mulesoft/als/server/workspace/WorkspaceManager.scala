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

import java.util.UUID
import java.util.concurrent.Semaphore
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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

  private val lock = new ManagerLock()

  def getWorkspace(uri: String): WorkspaceContentManager = {
    // todo: maybe implement own blocking mechanism in order to be able to ask if blocked
    // todo: another fix to prevent the Await is making this method return a Future
    val r = Await.result(for {
      _ <- lock.block()
    } yield {
      workspaces.find(ws => ws.containsFile(uri)).getOrElse(defaultWorkspace)
    }, Duration.Inf)
    lock.release()
    r
  }

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
    getWorkspace(uri.toAmfUri).getUnit(uri.toAmfUri)

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = getWorkspace(uri.toAmfUri)
    logger.debug(s"Notify $uri", "WorkspaceManager", "blocking")
    if (manager.configFile
          .map(_.toAmfUri)
          .contains(uri.toAmfUri)) {
      manager.withConfiguration(ReaderWorkspaceConfigurationProvider(manager))
      manager.stage(uri.toAmfUri, CHANGE_CONFIG)
    } else manager.stage(uri.toAmfUri, kind)
  }

  private def blockingWorkspaces[T](func: () => Future[T]): Future[Unit] = {
    val uuid = UUID.randomUUID().toString
    logger.debug("Blocking workspaces", "WorkspaceManager", "blockingWorkspaces")
    val workspaces = (this.workspaces :+ defaultWorkspace).filterNot(_.isTerminated)
    workspaces.foreach(wcm => wcm.stage(wcm.folder + uuid, BLOCK_WORKSPACE))
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

  def contentManagerConfiguration(manager: WorkspaceContentManager,
                                  mainSubUri: String,
                                  dependencies: Set[String],
                                  reader: Option[ConfigReader]): Future[Unit] =
    lock
      .block()
      .map(_ => {
        changeContentManagerConfiguration(manager, mainSubUri, dependencies, reader)
        lock.release()
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

  override def getProjectRootOf(uri: String): Option[String] =
    getWorkspace(uri).getRootFolderFor(uri)

  override def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit] = {
    // Drop all old workspaces
    for {
      _ <- lock.block()
      newWorkspaces <- Future {
        workspaces.clear()
        val newWorkspaces = extractCleanURIs(workspaceFolders)
        dependencies.foreach(d => d.withUnitAccessor(this))
        newWorkspaces
      }
      _ <- blockingWorkspaces(() => {
        Future.sequence(newWorkspaces.map(initializeWS))
      })
    } yield {
      lock.release()
    }
  }

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Unit = {
    val event          = params.event
    val deletedFolders = event.deleted.flatMap(_.uri)

    workspaces
      .filter(p => deletedFolders.contains(p.folder))
      .foreach(shutdownWS)
    for {
      _ <- lock.block()
      _ <- blockingWorkspaces(() => {
        Future.sequence(event.added.flatMap(_.uri).map(initializeWS))
      })
    } yield {
      lock.release()
    }
  }

  dependencies.foreach(d => d.withUnitAccessor(this))

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    getLastUnit(uri.toAmfUri, uuid).flatMap(_ =>
      getWorkspace(uri.toAmfUri).getRelationships(uri.toAmfUri).getDocumentLinks(uri.toAmfUri))

  override def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]] = {
    val workspace = getWorkspace(uri)
    workspace.mainFileUri match {
      case Some(mf) =>
        getLastUnit(mf, uuid)
          .flatMap(_ => workspace.getRelationships(mf).getAllDocumentLinks)
      case _ => Future.successful(Map.empty)
    }
  }

  override def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]] =
    getLastUnit(uri, uuid).flatMap(_ => getWorkspace(uri).getRelationships(uri).getAliases(uri))

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
    getLastUnit(uri, uuid)
      .flatMap(
        cu =>
          getWorkspace(uri)
            .getRelationships(uri)
            .getRelationships(uri)
            .map(rl => (cu, filterDuplicates(rl))))

  override def isInMainTree(uri: String): Boolean =
    getWorkspace(uri).isInMainTree(uri)

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

  class ManagerLock() {
    private val lock = new Semaphore(1, true)

    def block(): Future[Unit] = {
      Future({
        lock.acquire()
      })
    }

    def release(): Unit = {
      lock.release()
    }
  }
}
