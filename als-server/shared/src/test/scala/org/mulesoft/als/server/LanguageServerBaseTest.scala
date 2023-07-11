package org.mulesoft.als.server

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.internal.unsafe.PlatformSecrets
import amf.custom.validation.client.scala.{BaseProfileValidatorBuilder, ProfileValidatorExecutor}
import amf.custom.validation.internal.DummyValidatorExecutor
import org.mulesoft.als.server.FailedLogs.loggerFixture
import org.mulesoft.als.server.Flaky.flakyFixture
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeParams, CleanDiagnosticTreeRequestType}
import org.mulesoft.als.server.feature.serialization.SerializationParams
import org.mulesoft.als.server.modules.diagnostic.AlsPublishDiagnosticsParams
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.protocol.textsync.DidFocusParams
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbol,
  DocumentSymbolParams,
  DocumentSymbolRequestType,
  SymbolInformation
}
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.textsync._
import org.mulesoft.lsp.workspace.FileChangeType.FileChangeType
import org.mulesoft.lsp.workspace.{
  DidChangeWatchedFilesParams,
  DidChangeWorkspaceFoldersParams,
  ExecuteCommandParams,
  FileEvent,
  WorkspaceFoldersChangeEvent
}
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.{FutureOutcome, OptionValues}

import java.io.StringWriter
import scala.concurrent.Future
import scala.util.Failure

abstract class LanguageServerBaseTest
    extends AsyncFunSuite
    with PlatformSecrets
    with Matchers
    with OptionValues
    with ChangesWorkspaceConfiguration {

  implicit val logger: TestLogger = TestLogger()

  override def withFixture(test: NoArgAsyncTest): FutureOutcome =
    loggerFixture(test)(flakyFixture(_)(super.withFixture))

  object DummyProfileValidator extends BaseProfileValidatorBuilder {

    def validator(profile: DialectInstance): ProfileValidatorExecutor = {
      new ProfileValidatorExecutor(DummyValidatorExecutor, profile)
    }
  }

  protected val initializeParams: AlsInitializeParams = AlsInitializeParams.default

  private def telemetryNotifications(
      mockTelemetryClientNotifier: MockTelemetryClientNotifier
  )(qty: Int, previous: Seq[TelemetryMessage]): Future[Seq[TelemetryMessage]] = {
    if (qty < 0) Future(previous)
    else if (qty > 0)
      mockTelemetryClientNotifier.nextCall.flatMap(nc =>
        telemetryNotifications(mockTelemetryClientNotifier)(qty - 1, previous :+ nc)
      )
    else
      mockTelemetryClientNotifier.nextCall.map(nc => previous :+ nc)
  }

  def withTelemetry(
      mockTelemetryClientNotifier: MockTelemetryClientNotifier
  )(qty: Int, fn: () => Unit): Future[Seq[TelemetryMessage]] = {
    fn()
    telemetryNotifications(mockTelemetryClientNotifier)(qty - 1, Nil)
  }

  def openFileNotification(server: LanguageServer)(file: String, content: String): Future[Unit] =
    openFile(server)(file, content)

  def requestCleanDiagnostic(server: LanguageServer)(uri: String): Future[Seq[AlsPublishDiagnosticsParams]] =
    server
      .resolveHandler(CleanDiagnosticTreeRequestType)
      .value
      .apply(CleanDiagnosticTreeParams(TextDocumentIdentifier(uri)))

  def requestDocumentSymbol(
      server: LanguageServer
  )(uri: String): Future[Either[Seq[SymbolInformation], Seq[DocumentSymbol]]] =
    server
      .resolveHandler(DocumentSymbolRequestType)
      .value
      .apply(DocumentSymbolParams(TextDocumentIdentifier(uri)))

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[Unit] =
    onFocus(server)(file, version)

  def changeNotification(server: LanguageServer)(file: String, content: String, version: Int): Future[Unit] =
    changeFile(server)(file, content, version)

  def withServer[R](server: LanguageServer, initParams: AlsInitializeParams = initializeParams)(
      fn: LanguageServer => Future[R]
  ): Future[R] = {
    server
      .testInitialize(initParams)
      .flatMap(_ => {
        server.initialized()
        fn(server)
          .andThen { case Failure(exception) =>
            // if there was an error, then print out all the logs for this test
            while (logger.logList.nonEmpty) println(logger.logList.dequeue())
            fail(exception)
          }
      })
  }

  def setMainFile(server: LanguageServer)(workspace: String, mainFile: String): Future[AnyRef] =
    changeWorkspaceConfiguration(server)(changeConfigArgs(Some(mainFile), workspace))

  def openFile(server: LanguageServer)(uri: String, text: String): Future[Unit] =
    server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(TextDocumentItem(uri, "", 0, text)))

  def onFocus(server: LanguageServer)(uri: String, version: Int): Future[Unit] =
    server.textDocumentSyncConsumer.didFocus(DidFocusParams(uri, version))

  def closeFile(server: LanguageServer)(uri: String): Future[Unit] =
    server.textDocumentSyncConsumer.didClose(DidCloseTextDocumentParams(TextDocumentIdentifier(uri)))

  def changeFile(server: LanguageServer)(uri: String, text: String, version: Int): Future[Unit] =
    server.textDocumentSyncConsumer.didChange(
      DidChangeTextDocumentParams(
        VersionedTextDocumentIdentifier(uri, Some(version)),
        Seq(TextDocumentContentChangeEvent(text))
      )
    )

  def changeWatchedFiles(server: LanguageServer)(uri: String, changeType: FileChangeType): Future[Unit] =
    server.workspaceService.didChangeWatchedFiles(
      DidChangeWatchedFilesParams(List(FileEvent(uri, changeType)))
    )

  def rootPath: String

  def filePath(path: String): String = {
    s"file://als-server/shared/src/test/resources/$rootPath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }

  def addWorkspaceFolder(server: LanguageServer)(ws: WorkspaceFolder): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(List(ws), List()))
    )
  }

  def removeWorkspaceFolder(server: LanguageServer)(ws: WorkspaceFolder): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(List(), List(ws)))
    )
  }

  def didChangeWorkspaceFolders(
      server: LanguageServer
  )(added: List[WorkspaceFolder], removed: List[WorkspaceFolder]): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(added, removed))
    )
  }

  protected def serialize(
      server: LanguageServer,
      api: String,
      serializationProps: SerializationProps[StringWriter]
  ): Future[String] =
    server
      .resolveHandler(serializationProps.requestType)
      .value
      .apply(SerializationParams(TextDocumentIdentifier(api)))
      .map(_.model.toString)
}

trait ServerIndexGlobalDialectCommand extends LanguageServerBaseTest {
  def stringifyJson(str: String): String

  def indexGlobalDialect(server: LanguageServer, file: String, content: Option[String] = None): Future[Unit] = {
    def wrapJson(file: String, content: Option[String]): String =
      s"""{"uri": "$file" ${content.map(c => s""", "content": ${stringifyJson(c)}""").getOrElse("")}}"""

    val args = List(wrapJson(file, content))
    server.workspaceService
      .executeCommand(ExecuteCommandParams(Commands.INDEX_DIALECT, args))
      .map(_ => {
        Unit
      })
  }

  def indexGlobalDialect(server: LanguageServer, file: String, content: String): Future[Unit] =
    indexGlobalDialect(server, file, Some(content))

}
