package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.ByDirectoryTest
import org.mulesoft.als.configuration.ConfigurationStyle.COMMAND
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticImplicits.PublishDiagnosticsParamsWriter
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.{ChangesWorkspaceConfiguration, WorkspaceManager}
import org.mulesoft.als.server.{LanguageServerBuilder, MockDiagnosticClientNotifier, TestLogger}
import org.mulesoft.common.io.SyncFile
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.TextDocumentItem
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.Future

class NodeJsCustomValidationByDirectoryTest extends ByDirectoryTest with ChangesWorkspaceConfiguration {
  def rootPath: String                     = "custom-validation/byDirectory"
  val logger: TestLogger                   = TestLogger()
  val validator: AMFOpaValidator           = new JsCustomValidator(logger)
  override def fileExtensions: Seq[String] = Seq(".yaml")

  override def testFile(content: String, file: SyncFile, parent: String): Unit = {
    val workspaceFolder = s"file://${file.path.replace(file.name, "")}"
    val relativeUri     = s"${file.path.replace(file.name, "")}"
    val profileUri      = s"${workspaceFolder}profile.yaml"

    s"Run custom validation on ${file.parent}" - {
      s"Expect positive custom validation on ${file.parent}" in {
        runForPrefix(workspaceFolder, relativeUri, profileUri, "positive")
      }
      s"Expect negative custom validation on ${file.parent}" in {
        runForPrefix(workspaceFolder, relativeUri, profileUri, "negative")
      }
    }

  }

  private def runForPrefix(workspaceFolder: String, relativeUri: String, profileUri: String, prefix: String) = {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(12000)
    val (server, workspaceManager)                       = buildServer(diagnosticNotifier)
    implicit val s: LanguageServer                       = server
    for {
      _ <- server.initialize(
        AlsInitializeParams(None,
                            Some(TraceKind.Off),
                            rootUri = Some(workspaceFolder),
                            projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
      _ <- changeWorkspaceConfiguration(
        workspaceManager,
        changeConfigArgs(None, Some(workspaceFolder), Set.empty, Set(profileUri))) // register profile
      r <- runFor(relativeUri, workspaceFolder, prefix, diagnosticNotifier)
    } yield {
      r
    }
  }

  private val extensions = Seq("data", "yaml", "json")

  def runFor(relativeUri: String,
             workspaceFolder: String,
             prefix: String,
             diagnosticNotifier: MockDiagnosticClientNotifier)(implicit server: LanguageServer): Future[Assertion] = {
    val apiUri: String = extensions
      .map(e => s"$prefix.$e")
      .find(file => fs.syncFile(s"$relativeUri$file").exists)
      .map(file => s"$workspaceFolder$file")
      .getOrElse(fail(s"Failed to find valid file for $prefix"))
    val expectedUri = s"${workspaceFolder}expected/$prefix.yaml"
    for {
      diagnostics <- validateUri(server, diagnosticNotifier, apiUri)
      tmp         <- writeTemporaryFile(expectedUri)(diagnostics.write)
      r           <- assertDifferences(tmp, expectedUri)
    } yield {
      r
    }
  }

  def validateUri(server: LanguageServer,
                  notifier: MockDiagnosticClientNotifier,
                  uri: String): Future[PublishDiagnosticsParams] =
    for {
      p          <- platform.fetchContent(uri, AMFGraphConfiguration.predefined())
      _          <- openFile(server)(uri, p.toString())
      _          <- notifier.nextCall // Resolution diagnostic manager
      diagnostic <- notifier.nextCall // Custom validation manager
    } yield {
      diagnostic
    }

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): (LanguageServer, WorkspaceManager) = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm      = builder.buildDiagnosticManagers(Some(validator))
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(b.addInitializableModule)
    (b.build(), factory.workspaceManager)
  }

  def openFile(server: LanguageServer)(uri: String, text: String): Future[Unit] =
    server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(TextDocumentItem(uri, "", 0, text)))

  def filePath(path: String): String = {
    s"file://als-server/js/src/test/resources/$rootPath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }

  def dir: SyncFile = fs.syncFile(s"als-server/js/src/test/resources/$rootPath")

  s"NodeJS Custom Validation tests" - {
    forDirectory(dir, "", mustHaveMarker = false)
  }

  override def filterValidFiles(files: Array[SyncFile]): Array[SyncFile] =
    super.filterValidFiles(files).filter(_.name.contains("profile"))

}
