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
import org.mulesoft.lsp.feature.definition.{
  ClientDefinitionClientCapabilities,
  ClientDefinitionParams,
  DefinitionClientCapabilities,
  DefinitionParams
}
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.documentFormatting.{DocumentFormattingClientCapabilities, DocumentFormattingParams}
import org.mulesoft.lsp.feature.documentRangeFormatting.{
  DocumentRangeFormattingClientCapabilities,
  DocumentRangeFormattingParams
}
import org.mulesoft.lsp.feature.documenthighlight.{ClientDocumentHighlightCapabilities, ClientDocumentHighlightParams}
import org.mulesoft.lsp.feature.documentsymbol._
import org.mulesoft.lsp.feature.folding.{
  ClientFoldingRangeCapabilities,
  ClientFoldingRangeParams,
  FoldingRangeCapabilities,
  FoldingRangeParams
}
import org.mulesoft.lsp.feature.formatting.{
  ClientDocumentFormattingClientCapabilities,
  ClientDocumentFormattingParams,
  ClientDocumentRangeFormattingClientCapabilities,
  ClientDocumentRangeFormattingParams
}
import org.mulesoft.lsp.feature.highlight.{DocumentHighlightCapabilities, DocumentHighlightParams}
import org.mulesoft.lsp.feature.hover._
import org.mulesoft.lsp.feature.implementation.{
  ClientImplementationClientCapabilities,
  ClientImplementationParams,
  ImplementationClientCapabilities,
  ImplementationParams
}
import org.mulesoft.lsp.feature.link._
import org.mulesoft.lsp.feature.reference._
import org.mulesoft.lsp.feature.rename._
import org.mulesoft.lsp.feature.selection.{ClientSelectionRangeCapabilities, ClientSelectionRangeParams}
import org.mulesoft.lsp.feature.selectionRange.{SelectionRangeCapabilities, SelectionRangeParams}
import org.mulesoft.lsp.feature.telemetry.{
  ClientTelemetryClientCapabilities,
  ClientTelemetryMessage,
  TelemetryClientCapabilities,
  TelemetryMessage
}
import org.mulesoft.lsp.feature.typedefinition.{
  ClientTypeDefinitionClientCapabilities,
  ClientTypeDefinitionParams,
  TypeDefinitionClientCapabilities,
  TypeDefinitionParams
}
import org.mulesoft.lsp.textsync._
import org.mulesoft.lsp.workspace._

import scala.language.implicitConversions
import scala.scalajs.js.{JSON, |}

object LspConvertersClientToShared {
  // $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

  implicit class SymbolKindClientCapabilitiesConverter(v: ClientSymbolKindClientCapabilities) {
    def toShared: SymbolKindClientCapabilities =
      SymbolKindClientCapabilities(v.valueSet.map(i => SymbolKind(i)).toSet)
  }

  implicit class DocumentSymbolClientCapabilitiesConverter(v: ClientDocumentSymbolClientCapabilities) {
    def toShared: DocumentSymbolClientCapabilities =
      DocumentSymbolClientCapabilities(v.dynamicRegistration.toOption,
                                       v.symbolKind.toOption.map(_.toShared),
                                       v.hierarchicalDocumentSymbolSupport.toOption)
  }

  implicit class CommandConverter(v: ClientCommand) {
    def toShared: Command =
      Command(v.title, v.command, v.arguments.map(_.toSeq).toOption) // todo: arguments should be transformed to shared?
  }

  implicit class PositionConverter(v: ClientPosition) {
    def toShared: Position =
      Position(v.line, v.character)
  }

  implicit class RangeConverter(v: ClientRange) {
    def toShared: Range =
      Range(v.start.toShared, v.end.toShared)
  }

  implicit class LocationLinkConverter(v: ClientLocationLink) {
    def toShared: LocationLink =
      LocationLink(v.targetUri,
                   v.targetRange.toShared,
                   v.targetSelectionRange.toShared,
                   v.originSelectionRange.map(_.toShared).toOption)
  }

  implicit class TextDocumentIdentifierConverter(v: ClientTextDocumentIdentifier) {
    def toShared: TextDocumentIdentifier =
      TextDocumentIdentifier(v.uri)
  }

  implicit class VersionedTextDocumentIdentifierConverter(v: ClientVersionedTextDocumentIdentifier) {
    def toShared: VersionedTextDocumentIdentifier = {
      VersionedTextDocumentIdentifier(v.uri, Option(v.version))
    }
  }

  implicit class TextDocumentItemConverter(v: ClientTextDocumentItem) {
    def toShared: TextDocumentItem =
      TextDocumentItem(v.uri, v.languageId, v.version, v.text)
  }

  implicit class TextDocumentPositionParamsConverter(v: ClientTextDocumentPositionParams) {
    def toShared: TextDocumentPositionParams =
      TextDocumentPositionParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class TypeDefinitionParamsConverter(v: ClientTypeDefinitionParams) {
    def toShared: TypeDefinitionParams =
      TypeDefinitionParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class HoverClientCapabilitiesConverter(v: ClientHoverClientCapabilities) {
    def toShared: HoverClientCapabilities =
      HoverClientCapabilities(v.dynamicRegistration.toOption,
                              v.contentFormat.toOption.map(_.toSeq).getOrElse(Nil).map(c => MarkupKind.withName(c)))
  }

  implicit class HoverParamsConverter(v: ClientHoverParams) {
    def toShared: HoverParams =
      HoverParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class FoldingRangeCapabilitiesConverter(v: ClientFoldingRangeCapabilities) {
    def toShared: FoldingRangeCapabilities =
      FoldingRangeCapabilities(v.dynamicRegistration.toOption, v.rangeLimit.toOption, v.lineFoldingOnly.toOption)
  }

  implicit class FoldingRangeParamsConverter(v: ClientFoldingRangeParams) {
    def toShared: FoldingRangeParams =
      FoldingRangeParams(v.textDocument.toShared)
  }

  implicit class DefinitionParamsConverter(v: ClientDefinitionParams) {
    def toShared: DefinitionParams =
      DefinitionParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class ImplementationParamsConverter(v: ClientImplementationParams) {
    def toShared: ImplementationParams =
      ImplementationParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class TextDocumentClientCapabilitiesConverter(v: ClientTextDocumentClientCapabilities) {
    def toShared: TextDocumentClientCapabilities =
      TextDocumentClientCapabilities(
        v.synchronization.toOption.map(_.toShared),
        v.publishDiagnostics.toOption.map(_.toShared),
        v.completion.toOption.map(_.toShared),
        v.references.toOption.map(_.toShared),
        v.documentSymbol.toOption.map(_.toShared),
        v.definition.toOption.map(_.toShared),
        v.implementation.toOption.map(_.toShared),
        v.typeDefinition.toOption.map(_.toShared),
        v.rename.toOption.map(_.toShared),
        v.codeActionCapabilities.toOption.map(_.toShared),
        v.documentLink.toOption.map(_.toShared),
        v.hover.toOption.map(_.toShared),
        v.documentHighlight.toOption.map(_.toShared),
        v.foldingRange.toOption.map(_.toShared),
        v.selectionRange.toOption.map(_.toShared),
        v.documentFormatting.toOption.map(_.toShared),
        v.documentRangeFormatting.toOption.map(_.toShared)
      )
  }

  implicit class WorkspaceClientCapabilitiesConverter(v: ClientWorkspaceClientCapabilities) {
    def toShared: WorkspaceClientCapabilities =
      WorkspaceClientCapabilities(
        workspaceEdit = v.workspaceEdit.toOption.map(_.toShared)
      )
  }

  implicit class WorkspaceEditClientCapabilitiesConverter(v: ClientWorkspaceEditClientCapabilities) {
    def toShared: WorkspaceEditClientCapabilities =
      WorkspaceEditClientCapabilities(
        v.documentChanges.toOption
      )
  }

  implicit class ClientWorkspaceFolderServerCapabilitiesConverter(v: ClientWorkspaceFolderServerCapabilities) {
    def toShared: WorkspaceFolderServerCapabilities =
      WorkspaceFolderServerCapabilities(
        v.supported.toOption,
        v.changeNotifications.toOption.map((value: Any) =>
          value match {
            case value: String  => Left[String, Boolean](value)
            case value: Boolean => Right[String, Boolean](value)
        })
      )
  }

  implicit class ClientWorkspaceServerCapabilitiesConverter(v: ClientWorkspaceServerCapabilities) {
    def toShared: WorkspaceServerCapabilities =
      WorkspaceServerCapabilities(v.workspaceFolders.toOption.map(_.toShared))
  }

  implicit class StaticRegistrationOptionsConverter(v: ClientStaticRegistrationOptions) {
    def toShared: StaticRegistrationOptions =
      StaticRegistrationOptions(v.id.toOption)
  }

  implicit class WorkspaceFolderConverter(v: ClientWorkspaceFolder) {
    def toShared: WorkspaceFolder =
      WorkspaceFolder(v.uri.toOption, v.name.toOption)
  }

  implicit class NewFileOptionsConverter(v: ClientNewFileOptions) {
    def toShared: NewFileOptions =
      NewFileOptions(v.overwrite.toOption, v.ignoreIfExists.toOption)
  }

  implicit class ClientCreateFileConverter(v: ClientCreateFile) {
    def toShared: CreateFile =
      CreateFile(v.uri, v.options.map(_.toShared).toOption)
  }

  implicit class RenameFileConverter(v: ClientRenameFile) {
    def toShared: RenameFile =
      RenameFile(v.oldUri, v.newUri, v.options.map(_.toShared).toOption)
  }

  implicit class DeleteFileOptionsConverter(v: ClientDeleteFileOptions) {
    def toShared: DeleteFileOptions =
      DeleteFileOptions(v.recursive.toOption, v.ignoreIfNotExists.toOption)
  }

  implicit class DeleteFileConverter(v: ClientDeleteFile) {
    def toShared: DeleteFile =
      DeleteFile(v.uri, v.options.map(_.toShared).toOption)
  }

  implicit class TextEditConverter(v: ClientTextEdit) {
    def toShared: TextEdit =
      TextEdit(v.range.toShared, v.newText)
  }

  implicit class InsertReplaceEditConverter(v: ClientInsertReplaceEdit) {
    def toShared: InsertReplaceEdit =
      InsertReplaceEdit(v.newText, v.insert.toShared, v.replace.toShared)
  }

  implicit class TextDocumentEditConverter(v: ClientTextDocumentEdit) {
    def toShared: TextDocumentEdit =
      TextDocumentEdit(v.textDocument.toShared, v.edits.map(_.toShared).toSeq)
  }

  implicit class CompletionContextConverter(v: ClientCompletionContext) {
    def toShared: CompletionContext =
      CompletionContext(CompletionTriggerKind(v.triggerKind), v.triggerCharacter.toOption.flatMap(_.headOption))
  }

  implicit class LocationConverter(v: ClientLocation) {
    def toShared: Location =
      Location(v.uri, v.range.toShared)
  }

  implicit class DiagnosticClientCapabilitiesConverter(v: ClientDiagnosticClientCapabilities) {
    def toShared: DiagnosticClientCapabilities =
      DiagnosticClientCapabilities(v.relatedInformation.toOption)
  }

  implicit class DiagnosticRelatedInformationConverter(v: ClientDiagnosticRelatedInformation) {
    def toShared: DiagnosticRelatedInformation =
      DiagnosticRelatedInformation(v.location.toShared, v.message)
  }

  implicit class DiagnosticConverter(v: ClientDiagnostic) {
    def toShared: Diagnostic =
      Diagnostic(
        v.range.toShared,
        v.message,
        v.severity.map(s => DiagnosticSeverity(s)).toOption,
        v.code.toOption,
        v.codeDescription.toOption.map(_.href),
        v.source.toOption,
        v.relatedInformation.map(_.toShared).toSeq
      )
  }

  implicit class CompletionItemKindClientCapabilitiesConverter(v: ClientCompletionItemKindClientCapabilities) {
    def toShared: CompletionItemKindClientCapabilities =
      CompletionItemKindClientCapabilities(v.valueSet.map(s => CompletionItemKind(s)).toSet)
  }

  implicit class CompletionItemClientCapabilitiesConverter(v: ClientCompletionItemClientCapabilities) {
    def toShared: CompletionItemClientCapabilities =
      CompletionItemClientCapabilities(v.snippetSupport.toOption,
                                       v.commitCharactersSupport.toOption,
                                       v.deprecatedSupport.toOption,
                                       v.preselectSupport.toOption)
  }

  implicit class CompletionClientCapabilitiesConverter(v: ClientCompletionClientCapabilities) {
    def toShared: CompletionClientCapabilities =
      CompletionClientCapabilities(v.dynamicRegistration.toOption,
                                   v.completionItem.map(_.toShared).toOption,
                                   v.completionItemKind.map(_.toShared).toOption,
                                   v.contextSupport.toOption)
  }

  implicit class CompletionOptionsConverter(v: ClientCompletionOptions) {
    def toShared: CompletionOptions =
      CompletionOptions(v.resolveProvider.toOption, v.triggerCharacters.toOption.map(_.flatMap(_.headOption).toSet))
  }

  implicit class CompletionParamsConverter(v: ClientCompletionParams) {
    def toShared: CompletionParams =
      CompletionParams(v.textDocument.toShared, v.position.toShared, v.context.toOption.map(_.toShared))
  }

  implicit class CodeActionCapabilitiesConverter(v: ClientCodeActionCapabilities) {
    def toShared: CodeActionCapabilities =
      CodeActionCapabilities(v.dynamicRegistration.toOption,
                             v.codeActionLiteralSupport.toOption.map(_.toShared),
                             v.isPreferredSupport.toOption)
  }

  implicit class CodeActionContextConverter(v: ClientCodeActionContext) {
    def toShared: CodeActionContext =
      CodeActionContext(v.diagnostics.map(_.toShared).toSeq,
                        v.only.toOption.map(a => a.map(k => CodeActionKind(k)).toSeq))
  }

  implicit class CodeActionKindCapabilitiesConverter(v: ClientCodeActionKindCapabilities) {
    def toShared: CodeActionKindCapabilities =
      CodeActionKindCapabilities(v.valueSet.toList.map { codeActionOrEmpty })
  }

  private def codeActionOrEmpty(k: String) =
    try {
      CodeActionKind.withName(k)
    } catch {
      case _: NoSuchElementException => CodeActionKind.Empty
    }

  implicit class CodeActionLiteralSupportCapabilitiesConverter(v: ClientCodeActionLiteralSupportCapabilities) {
    def toShared: CodeActionLiteralSupportCapabilities =
      CodeActionLiteralSupportCapabilities(v.codeActionKind.toShared)
  }

  implicit class CodeActionOptionsConverter(v: ClientCodeActionOptions) {
    def toShared: CodeActionOptions =
      CodeActionRegistrationOptions(v.codeActionKinds.toOption.map(_.map { codeActionOrEmpty }.toSeq))
  }

  implicit class CodeActionParamsConverter(v: ClientCodeActionParams) {
    def toShared: CodeActionParams =
      CodeActionParams(v.textDocument.toShared, v.range.toShared, v.context.toShared)
  }

  implicit class DefinitionClientCapabilitiesConverter(v: ClientDefinitionClientCapabilities) {
    def toShared: DefinitionClientCapabilities =
      DefinitionClientCapabilities(v.dynamicRegistration.toOption, v.linkSupport.toOption)
  }

  implicit class ImplementationClientCapabilitiesConverter(v: ClientImplementationClientCapabilities) {
    def toShared: ImplementationClientCapabilities =
      ImplementationClientCapabilities(v.dynamicRegistration.toOption, v.linkSupport.toOption)
  }

  implicit class TypeDefinitionClientCapabilitiesConverter(v: ClientTypeDefinitionClientCapabilities) {
    def toShared: TypeDefinitionClientCapabilities =
      TypeDefinitionClientCapabilities(v.dynamicRegistration.toOption, v.linkSupport.toOption)
  }

  implicit class DocumentSymbolConverter(v: ClientDocumentSymbol) {
    def toShared: DocumentSymbol =
      DocumentSymbol(v.name,
                     SymbolKind(v.kind),
                     v.range.toShared,
                     v.selectionRange.toShared,
                     v.children.map(_.toShared).toSeq,
                     v.detail.toOption,
                     v.deprecated.toOption)
  }

  implicit class DocumentSymbolParamsConverter(v: ClientDocumentSymbolParams) {
    def toShared: DocumentSymbolParams =
      DocumentSymbolParams(v.textDocument.toShared)
  }

  implicit class SymbolInformationConverter(v: ClientSymbolInformation) {
    def toShared: SymbolInformation =
      SymbolInformation(v.name,
                        SymbolKind(v.kind),
                        v.location.toShared,
                        v.containerName.toOption,
                        v.deprecated.toOption)
  }

  implicit class DocumentLinkConverter(v: ClientDocumentLink) {
    def toShared: DocumentLink =
      DocumentLink(v.range.toShared, v.target, v.data.toOption)
  }

  implicit class DocumentLinkClientCapabilitiesConverter(v: ClientDocumentLinkClientCapabilities) {
    def toShared: DocumentLinkClientCapabilities =
      DocumentLinkClientCapabilities(v.dynamicRegistration.toOption, v.tooltipSupport.toOption)
  }

  implicit class DocumentLinkOptionsConverter(v: ClientDocumentLinkOptions) {
    def toShared: DocumentLinkOptions =
      DocumentLinkOptions(v.resolveProvider.toOption)
  }

  implicit class DocumentLinkParamsConverter(v: ClientDocumentLinkParams) {
    def toShared: DocumentLinkParams =
      DocumentLinkParams(v.textDocument.toShared)
  }

  implicit class ReferenceClientCapabilitiesConverter(v: ClientReferenceClientCapabilities) {
    def toShared: ReferenceClientCapabilities =
      ReferenceClientCapabilities(v.dynamicRegistration.toOption)
  }

  implicit class ReferenceContextConverter(v: ClientReferenceContext) {
    def toShared: ReferenceContext =
      ReferenceContext(v.includeDeclaration)
  }

  implicit class ReferenceParamsConverter(v: ClientReferenceParams) {
    def toShared: ReferenceParams =
      ReferenceParams(v.textDocument.toShared, v.position.toShared, v.context.toShared)
  }

  implicit class RenameClientCapabilitiesConverter(v: ClientRenameClientCapabilities) {
    def toShared: RenameClientCapabilities =
      RenameClientCapabilities(v.dynamicRegistration.toOption, v.prepareSupport.toOption)
  }

  implicit class RenameOptionsConverter(v: ClientRenameOptions) {
    def toShared: RenameOptions = RenameOptions(v.prepareProvider.toOption)
  }

  implicit class RenameParamsConverter(v: ClientRenameParams) {
    def toShared: RenameParams =
      RenameParams(v.textDocument.toShared, v.position.toShared, v.newName)
  }

  implicit class PrepareRenameParamsConverter(v: ClientPrepareRenameParams) {
    def toShared: PrepareRenameParams =
      PrepareRenameParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class TelemetryMessageConverter(v: ClientTelemetryMessage) {
    def toShared: TelemetryMessage =
      TelemetryMessage(v.event, v.messageType, v.message, v.uri, v.time.toLong, v.uuid)
  }

  implicit class TelemetryClientCapabilitiesConverter(v: ClientTelemetryClientCapabilities) {
    def toShared: TelemetryClientCapabilities =
      TelemetryClientCapabilities(v.relatedInformation.toOption)
  }

  implicit class DidChangeConfigurationNotificationParamsConverter(v: ClientDidChangeConfigurationNotificationParams) {
    def toShared: DidChangeConfigurationNotificationParams =
      DidChangeConfigurationNotificationParams(
        v.mainUri,
        v.folder.toOption,
        v.dependencies.map { d =>
          d.asInstanceOf[Any] match {
            case s: String => Left(s)
            case _         => Right(d.asInstanceOf[ClientDependencyConfiguration].toShared)
          }
        }.toSet
      )
  }

  implicit class DependencyConfigurationConverter(v: ClientDependencyConfiguration) {
    def toShared: DependencyConfiguration =
      DependencyConfiguration(v.file, v.scope)
  }

  implicit class DidChangeTextDocumentParamsConverter(v: ClientDidChangeTextDocumentParams) {
    def toShared: DidChangeTextDocumentParams =
      DidChangeTextDocumentParams(v.textDocument.toShared, v.contentChanges.map(_.toShared).toSeq)
  }

  implicit class DidCloseTextDocumentParamsConverter(v: ClientDidCloseTextDocumentParams) {
    def toShared: DidCloseTextDocumentParams =
      DidCloseTextDocumentParams(v.textDocument.toShared)
  }

  implicit class DidOpenTextDocumentParamsConverter(v: ClientDidOpenTextDocumentParams) {
    def toShared: DidOpenTextDocumentParams =
      DidOpenTextDocumentParams(v.textDocument.toShared)
  }

  implicit class SaveOptionsConverter(v: ClientSaveOptions) {
    def toShared: SaveOptions =
      SaveOptions(v.includeText.toOption)
  }

  implicit class SynchronizationClientCapabilitiesConverter(v: ClientSynchronizationClientCapabilities) {
    def toShared: SynchronizationClientCapabilities =
      SynchronizationClientCapabilities(v.dynamicRegistration.toOption,
                                        v.willSave.toOption,
                                        v.willSaveWaitUntil.toOption,
                                        v.didSave.toOption)
  }

  implicit class TextDocumentContentChangeEventConverter(v: ClientTextDocumentContentChangeEvent) {
    def toShared: TextDocumentContentChangeEvent =
      TextDocumentContentChangeEvent(v.text, v.range.map(_.toShared).toOption, v.rangeLength.toOption)
  }

  implicit class TextDocumentSyncOptionsConverter(v: ClientTextDocumentSyncOptions) {

    def unionToEither(value: ClientSaveOptions | Boolean): SaveOptions = value.asInstanceOf[Any] match {
      case value: Boolean => SaveOptions(Some(value))
      case _              => value.asInstanceOf[ClientSaveOptions].toShared
    }

    def toShared: TextDocumentSyncOptions =
      TextDocumentSyncOptions(
        v.openClose.toOption,
        v.change.toOption.map(k => TextDocumentSyncKind(k)),
        v.willSave.toOption,
        v.willSaveWaitUntil.toOption,
        v.save.toOption.map(unionToEither)
      )
  }

  implicit class DidChangeConfigurationParamsConverter(v: ClientDidChangeConfigurationParams) {
    def toShared: DidChangeConfigurationParams =
      DidChangeConfigurationParams(v.settings)
  }

  implicit class DidChangeWatchedFilesParamsConverter(v: ClientDidChangeWatchedFilesParams) {
    def toShared: DidChangeWatchedFilesParams =
      DidChangeWatchedFilesParams(v.changes.map(_.toShared).toList)
  }

  implicit class DidChangeWorkspaceFoldersParamsConverter(v: ClientDidChangeWorkspaceFoldersParams) {
    def toShared: DidChangeWorkspaceFoldersParams =
      DidChangeWorkspaceFoldersParams(v.event.toShared)
  }

  implicit class ExecuteCommandParamsConverter(v: ClientExecuteCommandParams) {
    def toShared: ExecuteCommandParams =
      ExecuteCommandParams(v.command, v.arguments.map(JSON.stringify(_)).toList)
  }

  implicit class FileEventConverter(v: ClientFileEvent) {
    def toShared: FileEvent =
      FileEvent(v.uri, FileChangeType(v.`type`))
  }

  implicit class WorkspaceFoldersChangeEventConverter(v: ClientWorkspaceFoldersChangeEvent) {
    def toShared: WorkspaceFoldersChangeEvent =
      WorkspaceFoldersChangeEvent(v.added.map(_.toShared).toList, v.deleted.map(_.toShared).toList)
  }

  implicit class WorkspaceSymbolParamsConverter(v: ClientWorkspaceSymbolParams) {
    def toShared: WorkspaceSymbolParams =
      WorkspaceSymbolParams(v.query)
  }

  implicit class DocumentHighlightParamsConverter(v: ClientDocumentHighlightParams) {
    def toShared: DocumentHighlightParams =
      DocumentHighlightParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class DocumentHighlightCapabilitiesConverter(v: ClientDocumentHighlightCapabilities) {
    def toShared: DocumentHighlightCapabilities =
      DocumentHighlightCapabilities(v.dynamicRegistration.toOption)
  }

  implicit class SelectionRangeParamsConverter(v: ClientSelectionRangeParams) {
    def toShared: SelectionRangeParams =
      SelectionRangeParams(v.textDocument.toShared, v.positions.map(_.toShared).toSeq)
  }

  implicit class SelectionRangeCapabilitiesConverter(v: ClientSelectionRangeCapabilities) {
    def toShared: SelectionRangeCapabilities =
      SelectionRangeCapabilities(v.dynamicRegistration.toOption)
  }

  implicit class FormattingOptionsConverter(v: ClientFormattingOptions) {
    def toShared: FormattingOptions =
      FormattingOptions(v.tabSize, v.insertSpaces.getOrElse(false))
  }

  implicit class DocumentFormattingParamsConverter(v: ClientDocumentFormattingParams) {
    def toShared: DocumentFormattingParams =
      DocumentFormattingParams(v.textDocument.toShared, v.options.toShared)
  }

  implicit class DocumentFormattingClientCapabilitiesConverter(v: ClientDocumentFormattingClientCapabilities) {
    def toShared: DocumentFormattingClientCapabilities =
      DocumentFormattingClientCapabilities(v.dynamicRegistration.toOption)
  }

  implicit class DocumentRangeFormattingParamsConverter(v: ClientDocumentRangeFormattingParams) {
    def toShared: DocumentRangeFormattingParams =
      DocumentRangeFormattingParams(v.textDocument.toShared, v.range.toShared, v.options.toShared)
  }

  implicit class DocumentRangeFormattingClientCapabilitiesConverter(v: ClientDocumentRangeFormattingClientCapabilities) {
    def toShared: DocumentRangeFormattingClientCapabilities =
      DocumentRangeFormattingClientCapabilities(v.dynamicRegistration.toOption)
  }
  // $COVERAGE-ON
}
