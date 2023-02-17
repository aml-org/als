package org.mulesoft.als.server

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.lsp.configuration.TraceKind
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.Future

abstract class ServerWithMarkerTest[Out] extends LanguageServerBaseTest with BeforeAndAfterEach with MarkerFinderTest {

  override protected def beforeEach(): Unit = notifier.promises.clear()

  val notifier: MockTelemetryParsingClientNotifier

  def runTest(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Out] =
    runTestMultipleMarkers(server, path, dialect).map(_.head)

  def runTestMultipleMarkers(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Seq[Out]] =
    withServer[Seq[Out]](server, AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(filePath("")))) {
      server =>
        val resolved = filePath(platform.encodeURI(path))

        for {
          _       <- dialect.map(openDialect(_, server)).getOrElse(Future.unit)
          content <- this.platform.fetchContent(resolved, AMFGraphConfiguration.predefined())
          definitions <- {
            val fileContentsStr = content.stream.toString
            val markersInfo     = this.findMarkers(fileContentsStr)
            markersInfo.headOption
              .map(markerInfo => {
                openFile(server)(resolved, markerInfo.content)
                  .flatMap(_ =>
                    notifier.nextCall.flatMap(_ => {
                      val result = Future.sequence(markersInfo.map(markerInfo => {
                        getAction(resolved, server, markerInfo)
                      }))
                      result
                        .flatMap(_ => closeFile(server)(path))
                        .flatMap(_ => result)
                    })
                  )
              })
              .getOrElse(Future.successful(Seq.empty))
          }
        } yield definitions
    }

  private def openDialect(path: String, server: LanguageServer): Future[Unit] = {
    val resolved = filePath(platform.encodeURI(path))
    changeWorkspaceConfiguration(server)(changeConfigArgs(None, filePath(""), dialects = Set(resolved))).map(_ => {})
  }

  def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Out]
}
