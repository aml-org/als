package org.mulesoft.als.server.modules.ast

import java.util.UUID

import amf.client.remote.Content
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.lsp.Initializable

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectManager(val unitsRepository: UnitsRepository,
                     astManager: AstManager,
                     dependencies: scala.collection.immutable.List[BaseUnitListener])
    extends AstNotifier[BaseUnit](dependencies)
    with TextListener
    with Initializable {
  override def initialize(): Future[Unit] = Future.successful()

  override def onFocus(uri: String): Unit = {
    val uuid = UUID.randomUUID().toString
    val fn = () => {
      unitsRepository.get(uri).flatMap {
        case None =>
          val eventualUnit: Future[BaseUnit] = astManager.forceGetCurrentAST(uri, uuid)
          unitsRepository.addUnit(uri, eventualUnit)
          eventualUnit.foreach(notify(_, uuid))
          unitsRepository.processing.remove(uri)
          eventualUnit.map(Left(_))
        case Some(other) => // ignore
          unitsRepository.processing.remove(uri)
          Future.successful(Right(other))

      }
    }
    unitsRepository.processing.update(uri, fn)
    fn()
  }

  override def trigger(uri: String): Unit = changeFile(uri)

  def changeFile(uri: String): Unit = {
    val uuid = UUID.randomUUID().toString
    val fn = () => {
      val eventualUnit = unitsRepository.get(uri).flatMap {
        case Some(ws) => // when is open file from workspace there is nothing to do
          val workspaceFuture = astManager.forceGetCurrentAST(ws.mainFileUri, uuid).map(Workspace)
          unitsRepository.add(uri, workspaceFuture)
          workspaceFuture.map(_.mainFile).foreach(e => notify(e, uuid))
          workspaceFuture.map(ws => Right(ws))
        case _ =>
          val eventualUnit = astManager.forceGetCurrentAST(uri, uuid)
          unitsRepository.addUnit(uri, eventualUnit)
          eventualUnit.foreach(e => notify(e, uuid))
          eventualUnit.map(e => Left(e))

      }
      eventualUnit.map(a => {
        unitsRepository.processing.remove(uri)
        a
      })
    }
    unitsRepository.processing.update(uri, fn)
    fn()
  }

  def openMF(uri: String): Future[Workspace] = {
    astManager.forceGetCurrentAST(uri, UUID.randomUUID().toString).map(Workspace)
  }

  override def indexDialect(uri: String, content: Option[String]): Unit = astManager.onIndexDialect(uri, content)
}

case class EditorEnvironment(memoryFiles: TextDocumentContainer,
                             unitsRepositories: UnitsRepository = UnitsRepository()) {

  val environment: Environment = Environment()
    .add(new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future { new Content(memoryFiles.getContent(resource), resource) }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = memoryFiles.exists(resource)
    })

  def forPatched(uri: String, patchedDocument: TextDocument): EditorEnvironment = {
    val newMemoryFiles = TextDocumentContainer(memoryFiles.platform)
    this.copy(memoryFiles = memoryFiles.patchUri(uri, patchedDocument))
  }
}

case class UnitsRepository(private val workspaces: mutable.Map[String, Future[Workspace]] = mutable.Map(),
                           orphanUnits: mutable.Map[String, Future[BaseUnit]] = mutable.Map()) {

  val processing: mutable.Map[String, () => Future[Either[BaseUnit, Workspace]]] = mutable.Map.empty

  def get(uri: String): Future[Option[Workspace]] = Future.find(workspaces.values.toList)(w => w.contains(uri))

  def findUnit(uri: String): Future[Option[BaseUnit]] = get(uri).map(w => w.flatMap(_.getDependency(uri)))

  def findGlobal(uri: String): Future[Option[BaseUnit]] = {
    processing.get(uri) match {
      case Some(fn) =>
        fn().map {
          case Right(ws) => ws.getDependency(uri)
          case Left(bu)  => Some(bu)
        }
      case _ =>
        findUnit(uri).flatMap {
          case None   => Future.sequence(orphanUnits.get(uri).toIterable).map(_.headOption)
          case option => Future.successful(option)
        }
    }
  }

  def add(rootUri: String, wsFuture: Future[Workspace]): Unit = {
    wsFuture.map(cleanOrphansWS)
    workspaces.update(rootUri, wsFuture)
  }

  private def cleanOrphansWS(ws: Workspace): Unit = {
    ws.tree.foreach(k => {
      if (orphanUnits.contains(k)) orphanUnits.remove(k)
    })
  }

  def addUnit(id: String, unit: Future[BaseUnit]): Unit = orphanUnits.update(id, unit)
}

object EditorEnvironment {
  def apply(platform: Platform): EditorEnvironment = new EditorEnvironment(TextDocumentContainer(platform))
}

case class Workspace(mainFile: BaseUnit) {

  type URI = String
  private def refs(bu: BaseUnit): Map[URI, BaseUnit] =
    bu.references.map(r => refs(r) + (r.id -> r)).reduce((a, b) => a ++ b)

  val units: Map[URI, BaseUnit] = refs(mainFile)
  val tree: Iterable[URI]       = units.keys

  val mainFileUri: URI = mainFile.id

  def contains(uri: URI): Boolean               = tree.exists(_ == uri)
  def getDependency(uri: URI): Option[BaseUnit] = if (mainFileUri == uri) Some(mainFile) else units.get(uri)
}
