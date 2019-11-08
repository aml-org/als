package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, CHANGE_FILE, NotificationKind}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.amfmanager.ParserHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Workspace(val folder: String,
                mainFile: Option[String],
                platform: Platform,
                environmentProvider: EnvironmentProvider,
                dependencies: List[BaseUnitListener]) {

  private var state: WorkspaceState                    = IDLE
  private var pending: Set[(String, NotificationKind)] = synchronized(Set.empty)
  private val repository                               = new Repository()

  def canProcess: Boolean = state == IDLE

  def dirty: Boolean = !canProcess

  def changedFile(uri: String, kind: NotificationKind): Unit = {
    enqueue(Set((uri, kind)))
    if (canProcess) process()
  }

  def initialize(): Unit = {
    mainFile.foreach(mf => processMFChanges(mf, environmentProvider.environmentSnapshot(), pending))
  }

  def process(): Unit = {
    val environment          = environmentProvider.environmentSnapshot()
    val (treeUnis, isolated) = pending.partition(u => repository.inTree(u._1)) // what if a new file is added between the partition and the override down
    val changedTreeUnits     = treeUnis.filter(_._2 == CHANGE_FILE)

    if (changedTreeUnits.nonEmpty) processMFChanges(mainFile.get, environment, pending)
    else if (isolated.nonEmpty) processIsolated(isolated.head._1, environment)
    else goIdle()
  }

  def getOrBuildUnit(uri: String): Future[CompilableUnit] = repository.getUnit(uri).map(toCompilableUnit)

  private def goIdle(): Unit = state = IDLE

  private def enqueue(files: Set[(String, NotificationKind)]): Unit = synchronized {
    pending = pending ++ files
  }

  private def dequeue(files: Set[String]): Unit = synchronized {
    pending = pending.filter(p => !files.contains(p._1))
  }

  private def processIsolated(file: String, environment: Environment) = {
    state = PROSSESING_FILE
    dequeue(Set(file))
    parse(file, environment).map { bu =>
      repository.update(file, bu, inTree = false)
      dependencies.foreach { d =>
        d.onNewAst(bu, UUID.randomUUID().toString)
      }
      process()
    }
  }

  private def processMFChanges(mainFile: String,
                               environment: Environment,
                               previouslyPending: Set[(String, NotificationKind)]): Future[Unit] = {
    state = PROSSESSING_PROJECT
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
    new ParserHelper(platform).parse(uri, environment)

  private def plainRef(bu: BaseUnit): Set[BaseUnit] = (bu +: bu.references.flatMap(plainRef)).toSet

  private def toCompilableUnit(parsedUnit: ParsedUnit): CompilableUnit =
    CompilableUnit(parsedUnit.bu.id,
                   parsedUnit.bu,
                   if (parsedUnit.inTree) mainFile else None,
                   this,
                   dirty = pending.exists(_._1 == parsedUnit.bu.id))

}

object MainFileReader {

  def read(folder: String): String = {
    ""
  }
}

case class CompilableUnit(uri: String, unit: BaseUnit, mainFile: Option[String], ws: Workspace, dirty: Boolean)

object CompilableUnit {
  def apply(bu: BaseUnit, workspace: Workspace): CompilableUnit =
    new CompilableUnit(bu.id, bu, None, workspace, false) // todo compute dirty?
}
