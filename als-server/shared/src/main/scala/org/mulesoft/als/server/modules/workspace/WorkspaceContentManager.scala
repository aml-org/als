package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, CHANGE_FILE, FOCUS_FILE, NotificationKind}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.ConfigFileMain
import org.mulesoft.amfmanager.ParserHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceContentManager(val folder: String,
                              configMainFile: Option[ConfigFileMain],
                              environmentProvider: EnvironmentProvider,
                              dependencies: List[BaseUnitListener]) {

  private var state: WorkspaceState                    = Idle
  private var pending: Set[(String, NotificationKind)] = synchronized(Set.empty)
  private val repository                               = new Repository()

  def canProcess: Boolean = state == Idle

  def dirty: Boolean = !canProcess

  def changedFile(uri: String, kind: NotificationKind): Unit = synchronized {
    enqueue(Set((uri, kind)))
    if (canProcess) process()
  }

  def initialize(): Unit =
    configMainFile.foreach(cmf => processMFChanges(cmf.mainFile, environmentProvider.environmentSnapshot(), pending))

  private def snapshot(): (Set[(String, NotificationKind)], Environment) = synchronized {
    val environment                             = environmentProvider.environmentSnapshot()
    val actual: Set[(String, NotificationKind)] = pending
    pending = Set.empty
    (actual, environment)
  }

  def process(): Unit = {
    val (actual, environment) = snapshot()

    val uuid = UUID.randomUUID().toString

    val (treeUnits, isolated) = actual.partition(u => repository.inTree(u._1)) // what if a new file is added between the partition and the override down
    val changedTreeUnits      = treeUnits.filter(_._2 == CHANGE_FILE)

    if (changedTreeUnits.nonEmpty) processMFChanges(configMainFile.get.mainFile, environment, actual)
    else if (isolated.nonEmpty) processIsolated(isolated.head._1, environment, uuid)
    else goIdle()
  }

  def getOrBuildUnit(uri: String, uuid: String): Future[CompilableUnit] = repository.getUnit(uri).map(toCompilableUnit)

  private def goIdle(): Unit = state = Idle

  private def enqueue(files: Set[(String, NotificationKind)]): Unit =
    pending = pending ++ files

  private def dequeue(files: Set[String]): Unit =
    pending = pending.filter(p => !files.contains(p._1))

  private def processIsolated(file: String, environment: Environment, uuid: String) = {
    state = ProssessingFile(file)
    dequeue(Set(file))
    parse(file, environment).map { bu =>
      repository.update(file, bu, inTree = false)
      dependencies.foreach { d =>
        d.onNewAst(bu, uuid)
      }
      process()
    }
  }

  private def processMFChanges(mainFile: String,
                               environment: Environment,
                               previouslyPending: Set[(String, NotificationKind)]): Future[Unit] = {
    state = ProssessinProject
    parse(mainFile, environment).map { u =>
      val newTree = plainRef(u).map(u => {
        repository.update(u.id, u, inTree = true)
        u.id
      })
      dependencies.foreach { d =>
        d.onNewAst(u, UUID.randomUUID().toString)
      }
      enqueue(previouslyPending.filter(t => !newTree.contains(t._1)))
      process()
    }
  }

  private def parse(uri: String, environment: Environment): Future[BaseUnit] =
    new ParserHelper(environmentProvider.platform).parse(uri, environment)

  private def plainRef(bu: BaseUnit): Set[BaseUnit] = (bu +: bu.references.flatMap(plainRef)).toSet

  private def toCompilableUnit(parsedUnit: ParsedUnit): CompilableUnit =
    CompilableUnit(parsedUnit.bu.id,
                   parsedUnit.bu,
                   if (parsedUnit.inTree) configMainFile.map(_.mainFile) else None,
                   this,
                   dirty = isDirty(parsedUnit.bu.id))

  private def isDirty(uri: String) = {
    state match {
      case Idle                      => false
      case ProssessinProject         => true
      case ProssessingFile(fileName) => uri == fileName
      case _                         => false
    }
  }

}

case class CompilableUnit(uri: String,
                          unit: BaseUnit,
                          mainFile: Option[String],
                          ws: WorkspaceContentManager,
                          dirty: Boolean)

object CompilableUnit {
  def apply(bu: BaseUnit, workspace: WorkspaceContentManager): CompilableUnit =
    new CompilableUnit(bu.id, bu, None, workspace, false) // todo compute dirty?
}
