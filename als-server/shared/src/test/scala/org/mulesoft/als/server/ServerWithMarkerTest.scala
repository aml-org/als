package org.mulesoft.als.server

import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.server.protocol.LanguageServer
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.Future

abstract class ServerWithMarkerTest[Out] extends LanguageServerBaseTest with BeforeAndAfterEach with MarkerFinderTest {

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
              openFile(server)(resolved, markerInfo.content)
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
}
