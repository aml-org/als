package org.mulesoft.als.server

import amf.core.internal.unsafe.PlatformSecrets
import com.google.gson.{Gson, GsonBuilder}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.logger.MessageSeverity.MessageSeverity
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
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, ExecuteCommandParams, WorkspaceFoldersChangeEvent}
import org.scalatest._

import java.io.StringWriter
import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Failure

abstract class LanguageServerBaseTest
    extends AsyncFunSuite
    with PlatformSecrets
    with Matchers
    with OptionValues
    with FailedLogs
    with ChangesWorkspaceConfiguration {

  protected val initializeParams: AlsInitializeParams = AlsInitializeParams.default

  private def telemetryNotifications(mockTelemetryClientNotifier: MockTelemetryClientNotifier)(
      qty: Int,
      previous: Seq[TelemetryMessage]): Future[Seq[TelemetryMessage]] = {
    if (qty < 0) Future(previous)
    else if (qty > 0)
      mockTelemetryClientNotifier.nextCall.flatMap(nc =>
        telemetryNotifications(mockTelemetryClientNotifier)(qty - 1, previous :+ nc))
    else
      mockTelemetryClientNotifier.nextCall.map(nc => previous :+ nc)
  }

  def withTelemetry(mockTelemetryClientNotifier: MockTelemetryClientNotifier)(
      qty: Int,
      fn: () => Unit): Future[Seq[TelemetryMessage]] = {
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

  def requestDocumentSymbol(server: LanguageServer)(
      uri: String): Future[Either[Seq[SymbolInformation], Seq[DocumentSymbol]]] =
    server
      .resolveHandler(DocumentSymbolRequestType)
      .value
      .apply(DocumentSymbolParams(TextDocumentIdentifier(uri)))

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[Unit] =
    onFocus(server)(file, version)

  def changeNotification(server: LanguageServer)(file: String, content: String, version: Int): Future[Unit] =
    changeFile(server)(file, content, version)

  def withServer[R](server: LanguageServer, initParams: AlsInitializeParams = initializeParams)(
      fn: LanguageServer => Future[R]): Future[R] = {
    server
      .initialize(initParams)
      .flatMap(_ => {
        server.initialized()
        fn(server)
          .andThen {
            case Failure(exception) =>
              // if there was an error, then print out all the logs for this test
              while (logger.logList.nonEmpty) println(logger.logList.dequeue())
              fail(exception)
          }
      })
  }

  def setMainFile(server: LanguageServer)(workspace: String, mainFile: String): Future[AnyRef] =
    changeWorkspaceConfiguration(server)(changeConfigArgs(Some(mainFile), Some(workspace)))

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
      ))

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

  def didChangeWorkspaceFolders(server: LanguageServer)(added: List[WorkspaceFolder],
                                                        removed: List[WorkspaceFolder]): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(added, removed))
    )
  }

  def indexGlobalDialect(server: LanguageServer, file: String, content: Option[String] = None): Future[Unit] = {
    def wrapJson(file: String, content: Option[String], gson: Gson): String =
      s"""{"uri": "$file" ${content.map(c => s""", "content": ${gson.toJson(c)}""").getOrElse("")}}"""

    val args = List(wrapJson(file, content, new GsonBuilder().create()))
    server.workspaceService
      .executeCommand(ExecuteCommandParams(Commands.INDEX_DIALECT, args))
      .map(_ => {
        Unit
      })
  }

  def indexGlobalDialect(server: LanguageServer, file: String, content: String): Future[Unit] =
    indexGlobalDialect(server, file, Some(content))

  protected def serialize(server: LanguageServer, api: String, serializationProps: SerializationProps[StringWriter]) = {
    server
      .resolveHandler(serializationProps.requestType)
      .value
      .apply(SerializationParams(TextDocumentIdentifier(api)))
      .map(_.model.toString)
  }

}

/**
  * mixin to clean logs in between tests
  */
trait FailedLogs extends AsyncTestSuiteMixin { this: AsyncTestSuite =>
  val logger = TestLogger()

  abstract override def withFixture(test: NoArgAsyncTest): FutureOutcome = {
    logger.logList.clear()
    logger.logList.enqueue(s"Starting test: ${test.name}")
    complete {
      super.withFixture(test) // To be stackable, must call super.withFixture
    } lastly {
      logger.logList.clear()
    }
  }
}

case class TestLogger() extends Logger {

  val logList: mutable.Queue[String] = mutable.Queue[String]()

  /**
    * Logs a message
    *
    * @param message      - message text
    * @param severity     - message severity
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit =
    synchronized(logList += s"log\n\t$message\n\t$severity\n\t$component\n\t$subComponent")

  /**
    * Logs a DEBUG severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def debug(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"debug\n\t$message\n\t$component\n\t$subComponent")

  /**
    * Logs a WARNING severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def warning(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"warning\n\t$message\n\t$component\n\t$subComponent")

  /**
    * Logs an ERROR severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def error(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"error\n\t$message\n\t$component\n\t$subComponent")
}
