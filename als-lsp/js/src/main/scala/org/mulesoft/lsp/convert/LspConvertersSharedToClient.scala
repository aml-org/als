package org.mulesoft.lsp.convert

import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.edit._
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.command.{ClientCommand, Command}
import org.mulesoft.lsp.feature.common.{
  ClientLocation,
  ClientLocationLink,
  ClientPosition,
  ClientRange,
  ClientTextDocumentIdentifier,
  ClientTextDocumentItem,
  ClientTextDocumentPositionParams,
  ClientVersionedTextDocumentIdentifier,
  Location,
  LocationLink,
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.definition.{ClientDefinitionClientCapabilities, DefinitionClientCapabilities}
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.documentFormatting.{DocumentFormattingClientCapabilities, DocumentFormattingParams}
import org.mulesoft.lsp.feature.documentRangeFormatting.{
  DocumentRangeFormattingClientCapabilities,
  DocumentRangeFormattingParams
}
import org.mulesoft.lsp.feature.documenthighlight.{ClientDocumentHighlight, ClientDocumentHighlightCapabilities}
import org.mulesoft.lsp.feature.documentsymbol._
import org.mulesoft.lsp.feature.folding.{
  ClientFoldingRange,
  ClientFoldingRangeCapabilities,
  FoldingRange,
  FoldingRangeCapabilities
}
import org.mulesoft.lsp.feature.formatting.{
  ClientDocumentFormattingClientCapabilities,
  ClientDocumentFormattingParams,
  ClientDocumentRangeFormattingClientCapabilities,
  ClientDocumentRangeFormattingParams
}
import org.mulesoft.lsp.feature.highlight.{DocumentHighlight, DocumentHighlightCapabilities}
import org.mulesoft.lsp.feature.hover.{ClientHover, ClientHoverClientCapabilities, Hover, HoverClientCapabilities}
import org.mulesoft.lsp.feature.implementation.{
  ClientImplementationClientCapabilities,
  ImplementationClientCapabilities
}
import org.mulesoft.lsp.feature.link._
import org.mulesoft.lsp.feature.reference._
import org.mulesoft.lsp.feature.rename._
import org.mulesoft.lsp.feature.selection.{ClientSelectionRange, ClientSelectionRangeCapabilities}
import org.mulesoft.lsp.feature.selectionRange.{SelectionRange, SelectionRangeCapabilities}
import org.mulesoft.lsp.feature.telemetry.{
  ClientTelemetryClientCapabilities,
  ClientTelemetryMessage,
  TelemetryClientCapabilities,
  TelemetryMessage
}
import org.mulesoft.lsp.feature.typedefinition.{
  ClientTypeDefinitionClientCapabilities,
  TypeDefinitionClientCapabilities
}
import org.mulesoft.lsp.textsync._
import org.mulesoft.lsp.workspace._

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|

object LspConvertersSharedToClient {

  implicit class ClientSymbolKindClientCapabilitiesConverter(v: SymbolKindClientCapabilities) {
    def toClient: ClientSymbolKindClientCapabilities =
      ClientSymbolKindClientCapabilities(v)
  }

  implicit class ClientDocumentSymbolClientCapabilitiesConverter(v: DocumentSymbolClientCapabilities) {
    def toClient: ClientDocumentSymbolClientCapabilities =
      ClientDocumentSymbolClientCapabilities(v)
  }

  implicit class ClientCommandConverter(v: Command) {
    def toClient: ClientCommand =
      ClientCommand(v)
  }

  implicit class ClientLocationLinkConverter(v: LocationLink) {
    def toClient: ClientLocationLink =
      ClientLocationLink(v)
  }

  implicit class ClientPositionConverter(v: Position) {
    def toClient: ClientPosition =
      ClientPosition(v)
  }

  implicit class ClientRangeConverter(v: Range) {
    def toClient: ClientRange =
      ClientRange(v)
  }

  implicit class ClientTextDocumentIdentifierConverter(v: TextDocumentIdentifier) {
    def toClient: ClientTextDocumentIdentifier =
      ClientTextDocumentIdentifier(v)
  }

  implicit class ClientVersionedTextDocumentIdentifierConverter(v: VersionedTextDocumentIdentifier) {
    def toClient: ClientVersionedTextDocumentIdentifier =
      ClientVersionedTextDocumentIdentifier(v)
  }

  implicit class ClientTextDocumentItemConverter(v: TextDocumentItem) {
    def toClient: ClientTextDocumentItem =
      ClientTextDocumentItem(v)
  }

  implicit class ClientTextDocumentPositionParamsConverter(v: TextDocumentPositionParams) {
    def toClient: ClientTextDocumentPositionParams =
      ClientTextDocumentPositionParams(v)
  }

  implicit class ClientTextDocumentClientCapabilitiesConverter(v: TextDocumentClientCapabilities) {
    def toClient: ClientTextDocumentClientCapabilities =
      ClientTextDocumentClientCapabilities(v)
  }

  implicit class ClientStaticRegistrationOptionsConverter(v: StaticRegistrationOptions) {
    def toClient: ClientStaticRegistrationOptions =
      ClientStaticRegistrationOptions(v)
  }

  implicit class ClientWorkDoneProgressOptionsConverter(v: WorkDoneProgressOptions) {
    def toClient: ClientWorkDoneProgressOptions =
      ClientWorkDoneProgressOptions(v)
  }

  implicit class ClientWorkspaceFolderConverter(v: WorkspaceFolder) {
    def toClient: ClientWorkspaceFolder =
      ClientWorkspaceFolder(v)
  }

  implicit class ClientNewFileOptionsConverter(v: NewFileOptions) {
    def toClient: ClientNewFileOptions =
      ClientNewFileOptions(v)
  }

  implicit class ClientCreateFileConverter(v: CreateFile) {
    def toClient: ClientCreateFile =
      ClientCreateFile(v)
  }

  implicit class ClientRenameFileConverter(v: RenameFile) {
    def toClient: ClientRenameFile =
      ClientRenameFile(v)
  }

  implicit class ClientDeleteFileOptionsConverter(v: DeleteFileOptions) {
    def toClient: ClientDeleteFileOptions =
      ClientDeleteFileOptions(v)
  }

  implicit class ClientDeleteFileConverter(v: DeleteFile) {
    def toClient: ClientDeleteFile =
      ClientDeleteFile(v)
  }

  implicit class ClientTextEditConverter(v: TextEdit) {
    def toClient: ClientTextEdit =
      ClientTextEdit(v)
  }

  implicit class ClientInsertReplaceEditConverter(v: InsertReplaceEdit) {
    def toClient: ClientInsertReplaceEdit =
      ClientInsertReplaceEdit(v)
  }

  implicit class ClientTextDocumentEditConverter(v: TextDocumentEdit) {
    def toClient: ClientTextDocumentEdit =
      ClientTextDocumentEdit(v)
  }

  implicit class ClientResourceOperationConverter(v: ResourceOperation) {
    def toClient: ClientResourceOperation =
      v match {
        case n: CreateFile => ClientCreateFile(n)
        case n: DeleteFile => ClientDeleteFile(n)
        case n: RenameFile => ClientRenameFile(n)
      }
  }

  implicit class ClientWorkspaceEditConverter(v: WorkspaceEdit) {
    def toClient: ClientWorkspaceEdit =
      ClientWorkspaceEdit(v)
  }

  implicit class ClientWorkspaceEditClientCapabilitiesConverter(v: WorkspaceEditClientCapabilities) {
    def toClient: ClientWorkspaceEditClientCapabilities =
      ClientWorkspaceEditClientCapabilities(v)
  }

  implicit class ClientCompletionContextConverter(v: CompletionContext) {
    def toClient: ClientCompletionContext =
      ClientCompletionContext(v)
  }

  implicit class ClientCompletionItemConverter(v: CompletionItem) {
    def toClient: ClientCompletionItem =
      ClientCompletionItem(v)
  }

  implicit class ClientLocationConverter(v: Location) {
    def toClient: ClientLocation =
      ClientLocation(v)
  }

  implicit class ClientDiagnosticClientCapabilitiesConverter(v: DiagnosticClientCapabilities) {
    def toClient: ClientDiagnosticClientCapabilities =
      ClientDiagnosticClientCapabilities(v)
  }

  implicit class ClientDiagnosticRelatedInformationConverter(v: DiagnosticRelatedInformation) {
    def toClient: ClientDiagnosticRelatedInformation =
      ClientDiagnosticRelatedInformation(v)
  }

  implicit class ClientDiagnosticConverter(v: Diagnostic) {
    def toClient: ClientDiagnostic =
      ClientDiagnostic(v)
  }

  implicit class ClientPublishDiagnosticsParamsConverter(v: PublishDiagnosticsParams) {
    def toClient: ClientPublishDiagnosticsParams =
      ClientPublishDiagnosticsParams(v)
  }

  implicit class ClientCompletionItemKindClientCapabilitiesConverter(v: CompletionItemKindClientCapabilities) {
    def toClient: ClientCompletionItemKindClientCapabilities =
      ClientCompletionItemKindClientCapabilities(v)
  }

  implicit class ClientCompletionItemClientCapabilitiesConverter(v: CompletionItemClientCapabilities) {
    def toClient: ClientCompletionItemClientCapabilities =
      ClientCompletionItemClientCapabilities(v)
  }

  implicit class ClientCompletionClientCapabilitiesConverter(v: CompletionClientCapabilities) {
    def toClient: ClientCompletionClientCapabilities =
      ClientCompletionClientCapabilities(v)
  }

  implicit class ClientCompletionListConverter(v: CompletionList) {
    def toClient: ClientCompletionList =
      ClientCompletionList(v)
  }

  implicit class ClientCompletionOptionsConverter(v: CompletionOptions) {
    def toClient: ClientCompletionOptions =
      ClientCompletionOptions(v)
  }

  implicit class ClientCompletionParamsConverter(v: CompletionParams) {
    def toClient: ClientCompletionParams =
      ClientCompletionParams(v)
  }

  implicit class ClientWorkspaceServerCapabilitiesConverter(v: WorkspaceServerCapabilities) {
    def toClient: ClientWorkspaceServerCapabilities =
      ClientWorkspaceServerCapabilities(v)
  }

  implicit class ClientWorkspaceFolderServerCapabilitiesConverter(v: WorkspaceFolderServerCapabilities) {
    def toClient: ClientWorkspaceFolderServerCapabilities =
      ClientWorkspaceFolderServerCapabilities(v)
  }

  implicit class ClientCodeActionConverter(v: CodeAction) {
    def toClient: ClientCodeAction =
      ClientCodeAction(v)
  }

  implicit class ClientCodeActionCapabilitiesConverter(v: CodeActionCapabilities) {
    def toClient: ClientCodeActionCapabilities =
      ClientCodeActionCapabilities(v)
  }

  implicit class ClientCodeActionContextConverter(v: CodeActionContext) {
    def toClient: ClientCodeActionContext =
      ClientCodeActionContext(v)
  }

  implicit class ClientCodeActionKindCapabilitiesConverter(v: CodeActionKindCapabilities) {
    def toClient: ClientCodeActionKindCapabilities =
      ClientCodeActionKindCapabilities(v)
  }

  implicit class ClientCodeActionLiteralSupportCapabilitiesConverter(v: CodeActionLiteralSupportCapabilities) {
    def toClient: ClientCodeActionLiteralSupportCapabilities =
      ClientCodeActionLiteralSupportCapabilities(v)
  }

  implicit class ClientCodeActionOptionsConverter(v: CodeActionOptions) {
    def toClient: ClientCodeActionOptions =
      ClientCodeActionOptions(v)
  }

  implicit class ClientCodeActionParamsConverter(v: CodeActionParams) {
    def toClient: ClientCodeActionParams =
      ClientCodeActionParams(v)
  }

  implicit class ClientDefinitionClientCapabilitiesConverter(v: DefinitionClientCapabilities) {
    def toClient: ClientDefinitionClientCapabilities =
      ClientDefinitionClientCapabilities(v)
  }

  implicit class ClientImplementationClientCapabilitiesConverter(v: ImplementationClientCapabilities) {
    def toClient: ClientImplementationClientCapabilities =
      ClientImplementationClientCapabilities(v)
  }

  implicit class ClientHoverConverter(v: Hover) {
    def toClient: ClientHover = ClientHover(v)
  }

  implicit class ClientFoldingRangeConverter(v: FoldingRange) {
    def toClient: ClientFoldingRange = ClientFoldingRange(v)
  }

  implicit class ClientFoldingRangesConverter(v: Seq[FoldingRange]) {
    def toClient: js.Array[ClientFoldingRange] = v.map(ClientFoldingRange(_)).toJSArray
  }

  implicit class ClientTypeDefinitionClientCapabilitiesConverter(v: TypeDefinitionClientCapabilities) {
    def toClient: ClientTypeDefinitionClientCapabilities =
      ClientTypeDefinitionClientCapabilities(v)
  }

  implicit class ClientDocumentSymbolConverter(v: DocumentSymbol) {
    def toClient: ClientDocumentSymbol =
      ClientDocumentSymbol(v)
  }

  implicit class ClientDocumentSymbolParamsConverter(v: DocumentSymbolParams) {
    def toClient: ClientDocumentSymbolParams =
      ClientDocumentSymbolParams(v)
  }

  implicit class ClientSymbolInformationConverter(v: SymbolInformation) {
    def toClient: ClientSymbolInformation =
      ClientSymbolInformation(v)
  }

  implicit class ClientDocumentLinkConverter(v: DocumentLink) {
    def toClient: ClientDocumentLink =
      ClientDocumentLink(v)
  }

  implicit class ClientDocumentHighlightConverter(v: DocumentHighlight) {
    def toClient: ClientDocumentHighlight =
      ClientDocumentHighlight(v)
  }

  implicit class ClientSelectionRangeConverter(v: SelectionRange) {
    def toClient: ClientSelectionRange =
      ClientSelectionRange(v)
  }

  implicit class ClientReferenceClientCapabilitiesConverter(v: ReferenceClientCapabilities) {
    def toClient: ClientReferenceClientCapabilities =
      ClientReferenceClientCapabilities(v)
  }

  implicit class ClientReferenceContextConverter(v: ReferenceContext) {
    def toClient: ClientReferenceContext =
      ClientReferenceContext(v)
  }

  implicit class ClientReferenceParamsConverter(v: ReferenceParams) {
    def toClient: ClientReferenceParams =
      ClientReferenceParams(v)
  }

  implicit class ClientDocumentLinkClientCapabilitiesConverter(v: DocumentLinkClientCapabilities) {
    def toClient: ClientDocumentLinkClientCapabilities =
      ClientDocumentLinkClientCapabilities(v)
  }

  implicit class ClientDocumentHighlightCapabilitiesConverter(v: DocumentHighlightCapabilities) {
    def toClient: ClientDocumentHighlightCapabilities =
      ClientDocumentHighlightCapabilities(v)
  }

  implicit class ClientHoverClientCapabilitiesConverter(v: HoverClientCapabilities) {
    def toClient: ClientHoverClientCapabilities =
      ClientHoverClientCapabilities(v)
  }

  implicit class ClientFoldingRangeCapabilitiesConverter(v: FoldingRangeCapabilities) {
    def toClient: ClientFoldingRangeCapabilities =
      ClientFoldingRangeCapabilities(v)
  }

  implicit class ClientSelectionRangeCapabilitiesConverter(v: SelectionRangeCapabilities) {
    def toClient: ClientSelectionRangeCapabilities =
      ClientSelectionRangeCapabilities(v)
  }

  implicit class ClientDocumentLinkOptionsConverter(v: DocumentLinkOptions) {
    def toClient: ClientDocumentLinkOptions =
      ClientDocumentLinkOptions(v)
  }

  implicit class ClientDocumentLinkParamsConverter(v: DocumentLinkParams) {
    def toClient: ClientDocumentLinkParams =
      ClientDocumentLinkParams(v)
  }

  implicit class ClientRenameClientCapabilitiesConverter(v: RenameClientCapabilities) {
    def toClient: ClientRenameClientCapabilities =
      ClientRenameClientCapabilities(v)
  }

  implicit class ClientRenameOptionsConverter(v: RenameOptions) {
    def toClient: ClientRenameOptions =
      ClientRenameOptions(v)
  }

  implicit class ClientPrepareRenameCompleteResultConverter(v: Either[Range, PrepareRenameResult]) {
    def toClient: ClientRange | ClientPrepareRenameResult =
      v match {
        case Right(r) => r.toClient
        case Left(l)  => l.toClient
      }
  }

  implicit class ClientPrepareRenameResultConverter(v: PrepareRenameResult) {
    def toClient: ClientPrepareRenameResult =
      ClientPrepareRenameResult(v)
  }

  implicit class ClientRenameParamsConverter(v: RenameParams) {
    def toClient: ClientRenameParams =
      ClientRenameParams(v)
  }

  implicit class ClientFormattingOptionsConverter(c: FormattingOptions) {
    def toClient: ClientFormattingOptions =
      ClientFormattingOptions(c)
  }

  implicit class ClientTelemetryMessageConverter(v: TelemetryMessage) {
    def toClient: ClientTelemetryMessage =
      ClientTelemetryMessage(v)
  }

  implicit class ClientTelemetryClientCapabilitiesConverter(v: TelemetryClientCapabilities) {
    def toClient: ClientTelemetryClientCapabilities =
      ClientTelemetryClientCapabilities(v)
  }

  implicit class ClientDidChangeConfigurationNotificationParamsConverter(v: DidChangeConfigurationNotificationParams) {
    def toClient: ClientDidChangeConfigurationNotificationParams =
      ClientDidChangeConfigurationNotificationParams(v)
  }

  implicit class ClientDependencyConfigurationConverter(v: DependencyConfiguration) {
    def toClient: ClientDependencyConfiguration =
      ClientDependencyConfiguration(v)
  }

  implicit class ClientDidChangeTextDocumentParamsConverter(v: DidChangeTextDocumentParams) {
    def toClient: ClientDidChangeTextDocumentParams =
      ClientDidChangeTextDocumentParams(v)
  }

  implicit class ClientDidCloseTextDocumentParamsConverter(v: DidCloseTextDocumentParams) {
    def toClient: ClientDidCloseTextDocumentParams =
      ClientDidCloseTextDocumentParams(v)
  }

  implicit class ClientDidOpenTextDocumentParamsConverter(v: DidOpenTextDocumentParams) {
    def toClient: ClientDidOpenTextDocumentParams =
      ClientDidOpenTextDocumentParams(v)
  }

  implicit class ClientSaveOptionsConverter(v: SaveOptions) {
    def toClient: ClientSaveOptions =
      ClientSaveOptions(v)
  }

  implicit class ClientSynchronizationClientCapabilitiesConverter(v: SynchronizationClientCapabilities) {
    def toClient: ClientSynchronizationClientCapabilities =
      ClientSynchronizationClientCapabilities(v)
  }

  implicit class ClientTextDocumentContentChangeEventConverter(v: TextDocumentContentChangeEvent) {
    def toClient: ClientTextDocumentContentChangeEvent =
      ClientTextDocumentContentChangeEvent(v)
  }

  implicit class ClientTextDocumentSyncOptionsConverter(v: TextDocumentSyncOptions) {
    def toClient: ClientTextDocumentSyncOptions =
      ClientTextDocumentSyncOptions(v)
  }

  implicit class ClientDidChangeConfigurationParamsConverter(v: DidChangeConfigurationParams) {
    def toClient: ClientDidChangeConfigurationParams =
      ClientDidChangeConfigurationParams(v)
  }

  implicit class ClientDidChangeWatchedFilesParamsConverter(v: DidChangeWatchedFilesParams) {
    def toClient: ClientDidChangeWatchedFilesParams =
      ClientDidChangeWatchedFilesParams(v)
  }

  implicit class ClientDidChangeWorkspaceFoldersParamsConverter(v: DidChangeWorkspaceFoldersParams) {
    def toClient: ClientDidChangeWorkspaceFoldersParams =
      ClientDidChangeWorkspaceFoldersParams(v)
  }

  implicit class ClientExecuteCommandParamsConverter(v: ExecuteCommandParams) {
    def toClient: ClientExecuteCommandParams =
      ClientExecuteCommandParams(v)
  }

  implicit class ClientFileEventConverter(v: FileEvent) {
    def toClient: ClientFileEvent =
      ClientFileEvent(v)
  }

  implicit class ClientWorkspaceFoldersChangeEventConverter(v: WorkspaceFoldersChangeEvent) {
    def toClient: ClientWorkspaceFoldersChangeEvent =
      ClientWorkspaceFoldersChangeEvent(v)
  }

  implicit class ClientWorkspaceSymbolParamsConverter(v: WorkspaceSymbolParams) {
    def toClient: ClientWorkspaceSymbolParams =
      ClientWorkspaceSymbolParams(v)
  }

  implicit class ClientDocumentFormattingParamsConverter(v: DocumentFormattingParams) {
    def toClient: ClientDocumentFormattingParams =
      ClientDocumentFormattingParams(v)
  }

  implicit class ClientDocumentFormattingClientCapabilitiesConverter(v: DocumentFormattingClientCapabilities) {
    def toClient: ClientDocumentFormattingClientCapabilities =
      ClientDocumentFormattingClientCapabilities(v)
  }

  implicit class ClientDocumentRangeFormattingParamsConverter(v: DocumentRangeFormattingParams) {
    def toClient: ClientDocumentRangeFormattingParams =
      ClientDocumentRangeFormattingParams(v)
  }

  implicit class ClientDocumentRangeFormattingClientCapabilitiesConverter(v: DocumentRangeFormattingClientCapabilities) {
    def toClient: ClientDocumentRangeFormattingClientCapabilities =
      ClientDocumentRangeFormattingClientCapabilities(v)
  }

  implicit class ClientCompletionResponseConverter(response: Either[Seq[CompletionItem], CompletionList]) {
    def toClient: ClientCompletionList | js.Array[ClientCompletionItem] = {
      if (response.isLeft)
        |.from[js.Array[ClientCompletionItem], ClientCompletionList, js.Array[ClientCompletionItem]](
          response.left.get.map(_.toClient).toJSArray)
      else
        response.right.get.toClient
    }
  }

  implicit class ClientEitherTextEditConverter(response: Either[TextEdit, InsertReplaceEdit]) {
    def toClient: ClientTextEdit | ClientInsertReplaceEdit = {
      if (response.isLeft)
        |.from[ClientTextEdit, ClientInsertReplaceEdit, ClientTextEdit](response.left.get.toClient)
      else
        response.right.get.toClient
    }
  }

  implicit class ClientDocumentSymbolResponseConverter(response: Either[Seq[SymbolInformation], Seq[DocumentSymbol]]) {
    def toClient: js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation] = {
      if (response.isLeft)
        |.from[js.Array[ClientSymbolInformation], js.Array[ClientDocumentSymbol], js.Array[ClientSymbolInformation]](
          response.left.get.map(_.toClient).toJSArray)
      else
        |.from[js.Array[ClientDocumentSymbol], js.Array[ClientDocumentSymbol], js.Array[ClientSymbolInformation]](
          response.right.get.map(_.toClient).toJSArray)
    }
  }

  implicit class ClientDefinitionResponseConverter(response: Either[Seq[Location], Seq[LocationLink]]) {
    def toClient: js.Array[ClientLocation] | js.Array[ClientLocationLink] = {
      if (response.isLeft)
        |.from[js.Array[ClientLocation], js.Array[ClientLocation], js.Array[ClientLocationLink]](
          response.left.get.map(_.toClient).toJSArray)
      else
        |.from[js.Array[ClientLocationLink], js.Array[ClientLocation], js.Array[ClientLocationLink]](
          response.right.get.map(_.toClient).toJSArray)
    }
  }

  def eitherToUnion[A, B](either: Either[A, B]): A | B = either fold (
    a => |.from[A, A, B](a),
    b => |.from[B, A, B](b)
  )

  def eitherToUnionWithMapping[A, B, C, D](aMapper: A => C, bMapper: B => D)(either: Either[A, B]): C | D =
    either fold (
      a => |.from[C, C, D](aMapper(a)),
      b => |.from[D, C, D](bMapper(b))
    )
}
