package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.common._
import org.mulesoft.als.client.lsp.configuration.{
  ClientClientCapabilities,
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
import org.mulesoft.als.client.lsp.feature.diagnostic.{
  ClientDiagnostic,
  ClientDiagnosticClientCapabilities,
  ClientDiagnosticRelatedInformation
}
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbolClientCapabilities,
  ClientSymbolKindClientCapabilities
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
  StaticRegistrationOptions,
  TextDocumentClientCapabilities,
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
import org.mulesoft.lsp.feature.diagnostic.{
  Diagnostic,
  DiagnosticClientCapabilities,
  DiagnosticRelatedInformation,
  DiagnosticSeverity
}
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbolClientCapabilities,
  SymbolKind,
  SymbolKindClientCapabilities
}

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

}
