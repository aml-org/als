package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.common._
import org.mulesoft.als.client.lsp.configuration.{
  ClientClientCapabilities,
  ClientInitializeParams,
  ClientInitializeResult,
  ClientServerCapabilities,
  ClientStaticRegistrationOptions,
  ClientTextDocumentClientCapabilities,
  ClientWorkspaceClientCapabilities,
  ClientWorkspaceFolder
}
import org.mulesoft.als.client.lsp.edit.{
  ClientCreateFile,
  ClientDeleteFile,
  ClientDeleteFileOptions,
  ClientNewFileOptions,
  ClientRenameFile,
  ClientTextDocumentEdit,
  ClientTextEdit,
  ClientWorkspaceEdit
}
import org.mulesoft.als.client.lsp.feature.codeactions.{
  ClientCodeAction,
  ClientCodeActionCapabilities,
  ClientCodeActionContext,
  ClientCodeActionKindCapabilities,
  ClientCodeActionLiteralSupportCapabilities,
  ClientCodeActionOptions,
  ClientCodeActionParams
}
import org.mulesoft.als.client.lsp.feature.completion.{
  ClientCompletionClientCapabilities,
  ClientCompletionContext,
  ClientCompletionItem,
  ClientCompletionItemClientCapabilities,
  ClientCompletionItemKindClientCapabilities,
  ClientCompletionList,
  ClientCompletionOptions,
  ClientCompletionParams
}
import org.mulesoft.als.client.lsp.feature.definition.ClientDefinitionClientCapabilities
import org.mulesoft.als.client.lsp.feature.diagnostic.{
  ClientDiagnostic,
  ClientDiagnosticClientCapabilities,
  ClientDiagnosticRelatedInformation
}
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbol,
  ClientDocumentSymbolClientCapabilities,
  ClientDocumentSymbolParams,
  ClientSymbolInformation,
  ClientSymbolKindClientCapabilities
}
import org.mulesoft.als.client.lsp.feature.link.{
  ClientDocumentLink,
  ClientDocumentLinkClientCapabilities,
  ClientDocumentLinkOptions,
  ClientDocumentLinkParams
}
import org.mulesoft.als.client.lsp.feature.reference.{
  ClientReferenceClientCapabilities,
  ClientReferenceContext,
  ClientReferenceParams
}
import org.mulesoft.als.client.lsp.feature.rename.{
  ClientRenameClientCapabilities,
  ClientRenameOptions,
  ClientRenameParams
}
import org.mulesoft.lsp.command.Command
import org.mulesoft.lsp.common.{
  Location,
  LocationLink,
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.configuration.{
  ClientCapabilities,
  InitializeParams,
  InitializeResult,
  ServerCapabilities,
  StaticRegistrationOptions,
  TextDocumentClientCapabilities,
  TraceKind,
  WorkspaceClientCapabilities,
  WorkspaceFolder
}
import org.mulesoft.lsp.edit.{
  CreateFile,
  DeleteFile,
  DeleteFileOptions,
  NewFileOptions,
  RenameFile,
  TextDocumentEdit,
  TextEdit,
  WorkspaceEdit
}
import org.mulesoft.lsp.feature.codeactions.{
  CodeAction,
  CodeActionCapabilities,
  CodeActionContext,
  CodeActionKind,
  CodeActionKindCapabilities,
  CodeActionLiteralSupportCapabilities,
  CodeActionOptions,
  CodeActionParams
}
import org.mulesoft.lsp.feature.completion.{
  CompletionClientCapabilities,
  CompletionContext,
  CompletionItem,
  CompletionItemClientCapabilities,
  CompletionItemKind,
  CompletionItemKindClientCapabilities,
  CompletionList,
  CompletionOptions,
  CompletionParams,
  CompletionTriggerKind,
  InsertTextFormat
}
import org.mulesoft.lsp.feature.definition.DefinitionClientCapabilities
import org.mulesoft.lsp.feature.diagnostic.{
  Diagnostic,
  DiagnosticClientCapabilities,
  DiagnosticRelatedInformation,
  DiagnosticSeverity
}
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbol,
  DocumentSymbolClientCapabilities,
  DocumentSymbolParams,
  SymbolInformation,
  SymbolKind,
  SymbolKindClientCapabilities
}
import org.mulesoft.lsp.feature.link.{
  DocumentLink,
  DocumentLinkClientCapabilities,
  DocumentLinkOptions,
  DocumentLinkParams
}
import org.mulesoft.lsp.feature.reference.{ReferenceClientCapabilities, ReferenceContext, ReferenceParams}
import org.mulesoft.lsp.feature.rename.{RenameClientCapabilities, RenameOptions, RenameParams}
import org.mulesoft.lsp.textsync.TextDocumentSyncKind

import scala.language.implicitConversions

object LspConvertersClientToShared {

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
    def toShared: VersionedTextDocumentIdentifier =
      VersionedTextDocumentIdentifier(v.uri, v.version.toOption)
  }

  implicit class TextDocumentItemConverter(v: ClientTextDocumentItem) {
    def toShared: TextDocumentItem =
      TextDocumentItem(v.uri, v.languageId, v.version, v.text)
  }

  implicit class TextDocumentPositionParamsConverter(v: ClientTextDocumentPositionParams) {
    def toShared: TextDocumentPositionParams =
      TextDocumentPositionParams(v.textDocument.toShared, v.position.toShared)
  }

  implicit class TextDocumentClientCapabilitiesConverter(v: ClientTextDocumentClientCapabilities) {
    def toShared: TextDocumentClientCapabilities =
      TextDocumentClientCapabilities()
  }

  implicit class WorkspaceClientCapabilitiesConverter(v: ClientWorkspaceClientCapabilities) {
    def toShared: WorkspaceClientCapabilities =
      WorkspaceClientCapabilities()
  }

  implicit class ClientCapabilitiesConverter(v: ClientClientCapabilities) {
    def toShared: ClientCapabilities =
      ClientCapabilities(v.workspace.map(_.toShared).toOption,
                         v.textDocument.map(_.toShared).toOption,
                         v.experimental.toOption)
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

  implicit class TextDocumentEditConverter(v: ClientTextDocumentEdit) {
    def toShared: TextDocumentEdit =
      TextDocumentEdit(v.textDocument.toShared, v.edits.map(_.toShared).toSeq)
  }

  implicit class WorkspaceEditConverter(v: ClientWorkspaceEdit) {
    def toShared: WorkspaceEdit =
      WorkspaceEdit(v.changes.mapValues(a => a.map(_.toShared).toSeq).toMap,
                    v.documentChanges.map(l => Left(l.toShared)).toSeq)
  }

  implicit class CompletionContextConverter(v: ClientCompletionContext) {
    def toShared: CompletionContext =
      CompletionContext(CompletionTriggerKind(v.triggerKind), v.triggerCharacter.toOption.flatMap(_.headOption))
  }

  implicit class CompletionItemConverter(v: ClientCompletionItem) {
    def toShared: CompletionItem =
      CompletionItem(
        v.label,
        v.kind.toOption.map(k => CompletionItemKind(k)),
        v.detail.toOption,
        v.documentation.toOption,
        v.deprecated.toOption,
        v.preselect.toOption,
        v.sortText.toOption,
        v.filterText.toOption,
        v.insertText.toOption,
        v.insertTextFormat.toOption.map(f => InsertTextFormat(f)),
        v.textEdit.toOption.map(_.toShared),
        v.additionalTextEdits.toOption.map(a => a.map(_.toShared).toSeq),
        v.commitCharacters.toOption.map(a => a.flatMap(_.headOption).toSeq)
      )
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

  implicit class CompletionListConverter(v: ClientCompletionList) {
    def toShared: CompletionList =
      CompletionList(v.items.map(_.toShared).toSeq, v.isIncomplete)
  }

  implicit class CompletionOptionsConverter(v: ClientCompletionOptions) {
    def toShared: CompletionOptions =
      CompletionOptions(v.resolveProvider.toOption, v.triggerCharacters.toOption.map(_.flatMap(_.headOption).toSet))
  }

  implicit class CompletionParamsConverter(v: ClientCompletionParams) {
    def toShared: CompletionParams =
      CompletionParams(v.textDocument.toShared, v.position.toShared, v.context.toOption.map(_.toShared))
  }

  implicit class InitializeParamsConverter(v: ClientInitializeParams) {
    def toShared: InitializeParams =
      InitializeParams(
        Some(v.capabilities.toShared),
        Some(TraceKind(v.trace)),
        v.rootUri.toOption,
        v.processId.toOption,
        v.workspaceFolders.toOption.map(a => a.map(_.toShared).toSeq),
        v.rootPath.toOption,
        v.initializationOptions.toOption
      )
  }

  implicit class ServerCapabilitiesConverter(v: ClientServerCapabilities) {
    def toShared: ServerCapabilities =
      ServerCapabilities(
        v.textDocumentSync.toOption.map(tdsk => Left(TextDocumentSyncKind(tdsk))),
        v.completionProvider.toOption.map(_.toShared),
        v.definitionProvider,
        v.referencesProvider,
        v.documentSymbolProvider,
        None,
        None,
        None,
        v.experimental.toOption
      )
  }

  implicit class InitializeResultConverter(v: ClientInitializeResult) {
    def toShared: InitializeResult =
      InitializeResult(v.capabilities.toShared)
  }

  implicit class CodeActionConverter(v: ClientCodeAction) {
    def toShared: CodeAction =
      CodeAction(
        v.title,
        v.kind.toOption.map(k => CodeActionKind(k)),
        v.diagnostics.toOption.map(a => a.map(_.toShared).toSeq),
        v.edit.toOption.map(_.toShared),
        v.command.toOption.map(_.toShared)
      )
  }

  implicit class CodeActionCapabilitiesConverter(v: ClientCodeActionCapabilities) {
    def toShared: CodeActionCapabilities =
      CodeActionCapabilities(v.dynamicRegistration.toOption, v.codeActionLiteralSupport.toShared)
  }

  implicit class CodeActionContextConverter(v: ClientCodeActionContext) {
    def toShared: CodeActionContext =
      CodeActionContext(v.diagnostics.map(_.toShared).toSeq,
                        v.only.toOption.map(a => a.map(k => CodeActionKind(k)).toSeq))
  }

  implicit class CodeActionKindCapabilitiesConverter(v: ClientCodeActionKindCapabilities) {
    def toShared: CodeActionKindCapabilities =
      CodeActionKindCapabilities(v.valueSet.toList)
  }

  implicit class CodeActionLiteralSupportCapabilitiesConverter(v: ClientCodeActionLiteralSupportCapabilities) {
    def toShared: CodeActionLiteralSupportCapabilities =
      CodeActionLiteralSupportCapabilities(v.codeActionKind.toShared)
  }

  implicit class CodeActionOptionsConverter(v: ClientCodeActionOptions) {
    def toShared: CodeActionOptions =
      CodeActionOptions(v.codeActionKinds.toOption.map(_.toSeq))
  }

  implicit class CodeActionParamsConverter(v: ClientCodeActionParams) {
    def toShared: CodeActionParams =
      CodeActionParams(v.textDocument.toShared, v.range.toShared, v.context.toShared)
  }

  implicit class DefinitionClientCapabilitiesConverter(v: ClientDefinitionClientCapabilities) {
    def toShared: DefinitionClientCapabilities =
      DefinitionClientCapabilities(v.dynamicRegistration.toOption, v.linkSupport.toOption)
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
}
