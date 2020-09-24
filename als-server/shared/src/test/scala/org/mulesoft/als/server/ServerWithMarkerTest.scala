package org.mulesoft.als.server

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.reference.MarkerInfo
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.Future

abstract class ServerWithMarkerTest[Out] extends LanguageServerBaseTest with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = notifier.promises.clear()

  val notifier: MockTelemetryParsingClientNotifier

  def runTest(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Out] =
    runTestMultipleMarkers(server, path, dialect).map(_.head)

  def runTestMultipleMarkers(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Seq[Out]] =
    withServer[Seq[Out]](server) { server =>
      val resolved = filePath(platform.encodeURI(path))

      for {
        _       <- dialect.map(openDialect(_, server)).getOrElse(Future.unit)
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markersInfo     = this.findMarkers(fileContentsStr)
          markersInfo.headOption
            .map(markerInfo => {
              openFile(server)(resolved, markerInfo.patchedContent.original)
              notifier.nextCall.flatMap(_ => {
                val result = Future.sequence(markersInfo.map(markerInfo => {
                  getAction(resolved, server, markerInfo)
                }))
                result.foreach(_ => closeFile(server)(path))
                result
              })
            })
            .getOrElse(Future.successful(Seq.empty))
        }
      } yield definitions
    }

  private def openDialect(path: String, server: LanguageServer): Future[Unit] = {
    val resolved = filePath(platform.encodeURI(path))
    this.platform.resolve(resolved).map(c => openFile(server)(resolved, c.stream.toString))
  }
  def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Out]

  def findMarker(str: String, label: String = "[*]", cut: Boolean = true): MarkerInfo = {
    findMarkers(str, label, cut).headOption
      .getOrElse(new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str)))
  }

  def findMarkers(str: String, label: String = "[*]", cut: Boolean = true): Seq[MarkerInfo] = {
    var markers    = Seq[Int]()
    var offset     = 0
    var rawContent = str

    do {
      offset = rawContent.indexOf(label, offset)
      if (offset >= 0) {
        markers = offset +: markers
        // I have no idea why we wouldn't want to cut
        if (cut) rawContent = rawContent.substring(0, offset) + rawContent.substring(offset + label.length)
      }
    } while (offset >= 0)

    markers.reverse.map(off => {
      val preparedContent =
        ContentPatcher(rawContent, off, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(off, rawContent))
    })
  }
}
