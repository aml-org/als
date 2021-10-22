package org.mulesoft.als.server

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.configuration.{ConfigurationStyle, ProjectConfigurationStyle}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.workspace.ExecuteCommandParams
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.Future

abstract class ServerWithMarkerTest[Out] extends LanguageServerBaseTest with BeforeAndAfterEach with MarkerFinderTest {

  override protected def beforeEach(): Unit = notifier.promises.clear()

  val notifier: MockTelemetryParsingClientNotifier

  def runTest(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Out] =
    runTestMultipleMarkers(server, path, dialect).map(_.head)

  def runTestMultipleMarkers(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Seq[Out]] =
    withServer[Seq[Out]](server,
                         AlsInitializeParams(None,
                                             Some(TraceKind.Off),
                                             projectConfigurationStyle =
                                               Some(ProjectConfigurationStyle(ConfigurationStyle.COMMAND)))) {
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
                    }))
              })
              .getOrElse(Future.successful(Seq.empty))
          }
        } yield definitions
    }

  private def openDialect(path: String, server: LanguageServer): Future[Unit] = {
    val resolved = filePath(platform.encodeURI(path))
    server.workspaceService
      .executeCommand(
        ExecuteCommandParams(
          Commands.DID_CHANGE_CONFIGURATION,
          List(s"""{"mainUri": "", "dependencies": [{"file": "$resolved", "scope": "dialect"}]}""")))
      .flatMap(_ => Future.unit)
  }

  def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Out]
}
