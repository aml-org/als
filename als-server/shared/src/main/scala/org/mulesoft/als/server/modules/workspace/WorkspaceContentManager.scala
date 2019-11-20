package org.mulesoft.als.server.modules.workspace

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.ConfigFileMain
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceContentManager(val folder: String,
                              configMainFile: Option[ConfigFileMain],
                              environmentProvider: EnvironmentProvider,
                              telemetryProvider: TelemetryProvider,
                              dependencies: List[BaseUnitListener]) {

  private var state: WorkspaceState    = Idle
  private val stagingArea: StagingArea = new StagingArea(environmentProvider)
  private val repository               = new Repository()
  private var current: Future[Unit]    = Future.unit

  def canProcess: Boolean = state == Idle

  def dirty: Boolean = !canProcess

  def changedFile(uri: String, kind: NotificationKind): Unit = synchronized {
    stagingArea.enqueue(Set((uri, kind)))
    if (canProcess) current = process()
  }

  def initialize(): Unit = {
    val snapshot = stagingArea.snapshot()
    configMainFile.foreach(cmf => {
      val f = processMFChanges(cmf.mainFile, snapshot)
      current = f
      f.map(_ => current = process())
    })
  }

  def process(): Future[Unit] = {
    if (stagingArea.isPending()) {
      val snapshot: Snapshot = stagingArea.snapshot()

      val (treeUnits, isolated) = snapshot.files.partition(u => repository.inTree(u._1)) // what if a new file is added between the partition and the override down
      val changedTreeUnits      = treeUnits.filter(tu => tu._2 == CHANGE_FILE || tu._2 == CLOSE_FILE)

      val f =
        if (changedTreeUnits.nonEmpty) processMFChanges(configMainFile.get.mainFile, snapshot)
        else processIsolatedChanges(isolated, snapshot.environment)

      f.map { u =>
        current = process()
        u
      }
    } else
      goIdle()
  }

  def getCompilableUnit(uri: String): Future[CompilableUnit] = {
    repository.getParsed(uri) match {
      case Some(pu) => Future.successful(buildCU(pu, getNext(uri)))
      case _ =>
        getNext(uri)
          .orElse(repository.getParsed(uri).map(pu => Future.successful(buildCU(pu, None))))
          .getOrElse(Future.failed(throw new Exception("failed unit")))
    }
  }

  private def buildCU(pu: ParsedUnit, next: Option[Future[CompilableUnit]]) =
    CompilableUnit(pu.bu.id, pu.bu, if (pu.inTree) configMainFile.map(_.mainFile) else None, next)

  private def getNext(uri: String): Option[Future[CompilableUnit]] = {
    current match {
      case Future.unit => None
      case _           => Some(current.flatMap(_ => getCompilableUnit(uri)))
    }
  }

  private def goIdle(): Future[Unit] = {
    state = Idle
    Future.unit
  }

  private def processIsolatedChanges(files: Set[(String, NotificationKind)], environment: Environment): Future[Unit] = {
    val (closedFiles, changedFiles) = files.partition(_._2 == CLOSE_FILE)
    cleanFiles(closedFiles)

    if (changedFiles.nonEmpty) processIsolated(files.head._1, environment, UUID.randomUUID().toString)
    else Future.successful(Unit)
  }

  private def processIsolated(file: String, environment: Environment, uuid: String): Future[Unit] = {
    state = ProcessingFile(file)
    stagingArea.dequeue(Set(file))
    parse(file, environment, uuid)
      .map { bu =>
        repository.update(file, bu, inTree = false)
        dependencies.foreach(_.onNewAst(bu, uuid))
      }
  }

  private def cleanFiles(closedFiles: Set[(String, NotificationKind)]): Unit =
    closedFiles.foreach(cf => dependencies.foreach(_.onRemoveFile(cf._1)))

  private def processMFChanges(mainFile: String, snapshot: Snapshot): Future[Unit] = {
    state = ProcessingProject
    val uuid = UUID.randomUUID().toString
    parse(s"$folder/$mainFile", snapshot.environment, uuid)
      .map { u =>
        val newTree = plainRef(u).map(u => {
          repository.update(u.id, u, inTree = true)
          u.id
        })
        dependencies.foreach(_.onNewAst(u, uuid))
        stagingArea.enqueue(snapshot.files.filter(t => !newTree.contains(t._1)))
      }
  }

  private def parse(uri: String, environment: Environment, uuid: String): Future[BaseUnit] = {
    telemetryProvider.addTimedMessage("Start AMF Parse", MessageTypes.BEGIN_PARSE, uri, uuid)
    val eventualUnit = new ParserHelper(environmentProvider.platform)
      .parse(FileUtils.getDecodedUri(uri, environmentProvider.platform), environment)
    eventualUnit.foreach(_ => telemetryProvider.addTimedMessage("End AMF Parse", MessageTypes.END_PARSE, uri, uuid))
    eventualUnit
  }

  private def plainRef(bu: BaseUnit): Set[BaseUnit] = (bu +: bu.references.flatMap(plainRef)).toSet

  private def isDirty(uri: String) = {
    state match {
      case Idle                     => false
      case ProcessingProject        => true
      case ProcessingFile(fileName) => uri == fileName
      case _                        => false
    }
  }

}

case class CompilableUnit(uri: String, unit: BaseUnit, mainFile: Option[String], next: Option[Future[CompilableUnit]]) {

  def getLast: Future[CompilableUnit] = {
    next match {
      case Some(f) => f.flatMap(cu => cu.getLast)
      case _       => Future.successful(this)
    }
  }
}
