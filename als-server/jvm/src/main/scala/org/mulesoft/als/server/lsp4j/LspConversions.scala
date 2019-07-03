package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.lsp.common.{
  Position,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier,
  Range
}
import org.mulesoft.lsp.common.{Location, Position, Range, TextDocumentIdentifier, TextDocumentItem, TextDocumentPositionParams, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.configuration.TraceKind.TraceKind
import org.mulesoft.lsp.configuration.{
  ClientCapabilities,
  InitializeParams,
  InitializeResult,
  ServerCapabilities,
  TextDocumentClientCapabilities,
  TraceKind,
  WorkspaceClientCapabilities,
  WorkspaceFolder
}
import org.mulesoft.lsp.feature.completion.{
  CompletionClientCapabilities,
  CompletionContext,
  CompletionItemClientCapabilities,
  CompletionItemKind,
  CompletionItemKindClientCapabilities,
  CompletionOptions,
  CompletionParams,
  CompletionTriggerKind
}
import org.mulesoft.lsp.configuration.{TraceKind, _}
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.completion.CompletionItemKind.CompletionItemKind
import org.mulesoft.lsp.feature.completion.CompletionTriggerKind.CompletionTriggerKind
import org.mulesoft.lsp.feature.definition.DefinitionClientCapabilities
import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbolClientCapabilities,
  DocumentSymbolParams,
  SymbolKind,
  SymbolKindClientCapabilities
}
import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticClientCapabilities, DiagnosticRelatedInformation, DiagnosticSeverity}
import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolClientCapabilities, DocumentSymbolParams, SymbolKind, SymbolKindClientCapabilities}
import org.mulesoft.lsp.feature.reference.{ReferenceClientCapabilities, ReferenceContext, ReferenceParams}
import org.mulesoft.lsp.feature.rename.{RenameClientCapabilities, RenameOptions, RenameParams}
import org.mulesoft.lsp.textsync.{
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  SaveOptions,
  SynchronizationClientCapabilities,
  TextDocumentContentChangeEvent,
  TextDocumentSyncKind,
  TextDocumentSyncOptions
}
import org.mulesoft.lsp.feature.codeactions.{CodeActionContext, CodeActionKind, CodeActionOptions, CodeActionParams}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity.DiagnosticSeverity
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object LspConversions {

  implicit def either[A, B, C, D](either: JEither[A, B], leftTo: A => C, rightTo: B => D): Either[C, D] =
    if (either.isLeft) Left(leftTo(either.getLeft)) else Right(rightTo(either.getRight))

  def booleanOrFalse(value: java.lang.Boolean): Boolean = !(value == null) && value

  implicit def synchronizationClientCapabilities(
      capabilities: lsp4j.SynchronizationCapabilities): SynchronizationClientCapabilities =
    SynchronizationClientCapabilities(
      Option(capabilities.getDynamicRegistration),
      Option(capabilities.getWillSave),
      Option(capabilities.getWillSaveWaitUntil),
      Option(capabilities.getDidSave)
    )

  implicit def diagnosticClientCapabilities(
      capabilities: lsp4j.PublishDiagnosticsCapabilities): DiagnosticClientCapabilities =
    DiagnosticClientCapabilities(Option(capabilities.getRelatedInformation))

  implicit def completionItemKind(kind: lsp4j.CompletionItemKind): CompletionItemKind =
    CompletionItemKind(kind.getValue)

  implicit def completionItemKindClientCapabilities(
      capabilities: lsp4j.CompletionItemKindCapabilities): CompletionItemKindClientCapabilities =
    CompletionItemKindClientCapabilities(
      Option(capabilities.getValueSet).map(_.asScala.map(completionItemKind).toSet).getOrElse(Set())
    )

  implicit def completionItemClientCapabilities(
      capabilities: lsp4j.CompletionItemCapabilities): CompletionItemClientCapabilities =
    CompletionItemClientCapabilities(
      Option(capabilities.getSnippetSupport),
      Option(capabilities.getCommitCharactersSupport),
      Option(capabilities.getDeprecatedSupport),
      Option(capabilities.getPreselectSupport)
    )

  implicit def completionClientCapabilities(capabilities: lsp4j.CompletionCapabilities): CompletionClientCapabilities =
    CompletionClientCapabilities(
      Option(capabilities.getDynamicRegistration),
      Option(capabilities.getCompletionItem).map(completionItemClientCapabilities),
      Option(capabilities.getCompletionItemKind).map(completionItemKindClientCapabilities),
      Option(capabilities.getContextSupport)
    )

  implicit def referenceClientCapabilities(capabilities: lsp4j.ReferencesCapabilities): ReferenceClientCapabilities =
    ReferenceClientCapabilities(Option(capabilities.getDynamicRegistration))

  implicit def symbolKind(capabilities: lsp4j.SymbolKind): SymbolKind =
    SymbolKind(capabilities.getValue)

  implicit def symbolKindClientCapabilities(capabilities: lsp4j.SymbolKindCapabilities): SymbolKindClientCapabilities =
    SymbolKindClientCapabilities(
      Option(capabilities.getValueSet).map(_.asScala.map(symbolKind).toSet).getOrElse(Set()))

  implicit def documentSymbolClientCapabilities(
      capabilities: lsp4j.DocumentSymbolCapabilities): DocumentSymbolClientCapabilities =
    DocumentSymbolClientCapabilities(
      Option(capabilities.getDynamicRegistration),
      Option(capabilities.getSymbolKind).map(symbolKindClientCapabilities),
      Option(capabilities.getHierarchicalDocumentSymbolSupport)
    )

  implicit def definitionClientCapabilities(capabilities: lsp4j.DefinitionCapabilities): DefinitionClientCapabilities =
    DefinitionClientCapabilities(Option(capabilities.getDynamicRegistration), None)

  implicit def renameClientCapabilities(capabilities: lsp4j.RenameCapabilities): RenameClientCapabilities =
    RenameClientCapabilities(
      Option(capabilities.getDynamicRegistration),
      Option(capabilities.getPrepareSupport)
    )

  implicit def textDocumentClientCapabilities(
      capabilities: lsp4j.TextDocumentClientCapabilities): TextDocumentClientCapabilities =
    TextDocumentClientCapabilities(
      Option(capabilities.getSynchronization).map(synchronizationClientCapabilities),
      Option(capabilities.getPublishDiagnostics).map(diagnosticClientCapabilities),
      Option(capabilities.getCompletion).map(completionClientCapabilities),
      Option(capabilities.getReferences).map(referenceClientCapabilities),
      Option(capabilities.getDocumentSymbol).map(documentSymbolClientCapabilities),
      Option(capabilities.getDefinition).map(definitionClientCapabilities),
      Option(capabilities.getRename).map(renameClientCapabilities)
    )

  implicit def workspaceClientCapabilities(
      capabilities: lsp4j.WorkspaceClientCapabilities): WorkspaceClientCapabilities =
    WorkspaceClientCapabilities()

  implicit def clientCapabilities(capabilities: lsp4j.ClientCapabilities): ClientCapabilities =
    ClientCapabilities(
      Option(capabilities.getWorkspace).map(workspaceClientCapabilities),
      Option(capabilities.getTextDocument).map(textDocumentClientCapabilities)
    )

  implicit def traceKind(kind: String): TraceKind = TraceKind.withName(kind)

  implicit def workspaceFolder(folder: lsp4j.WorkspaceFolder): WorkspaceFolder =
    WorkspaceFolder(Option(folder.getUri), Option(folder.getName))

  implicit def initializeParams(params: lsp4j.InitializeParams): InitializeParams =
    Option(params).map { p =>
      InitializeParams(
        Option(p.getCapabilities).map(clientCapabilities),
        Option(p.getTrace).map(traceKind),
        Option(p.getRootUri),
        Option(p.getProcessId),
        Option(p.getWorkspaceFolders).map(_.asScala.map(workspaceFolder)),
        Option(p.getRootPath),
        Option(p.getInitializationOptions)
      )
    } getOrElse InitializeParams.default

  implicit def textDocumentSyncKind(kind: lsp4j.TextDocumentSyncKind): TextDocumentSyncKind = kind match {
    case lsp4j.TextDocumentSyncKind.Full        => TextDocumentSyncKind.Full
    case lsp4j.TextDocumentSyncKind.Incremental => TextDocumentSyncKind.Incremental
    case lsp4j.TextDocumentSyncKind.None        => TextDocumentSyncKind.None
  }

  implicit def saveOptions(options: lsp4j.SaveOptions): SaveOptions =
    SaveOptions(Option(options.getIncludeText))

  implicit def textDocumentSyncOptions(options: lsp4j.TextDocumentSyncOptions): TextDocumentSyncOptions =
    TextDocumentSyncOptions(
      Option(options.getOpenClose),
      Option(options.getChange).map(textDocumentSyncKind),
      Option(options.getWillSave),
      Option(options.getWillSaveWaitUntil),
      Option(options.getSave).map(saveOptions)
    )

  implicit def renameOptions(options: lsp4j.RenameOptions): RenameOptions =
    RenameOptions(Option(options.getPrepareProvider))

  implicit def eitherRenameOptions(options: JEither[java.lang.Boolean, lsp4j.RenameOptions]): Option[RenameOptions] =
    either(options, booleanOrFalse, renameOptions)
      .fold(value => if (value) Some(RenameOptions()) else None, Some.apply)

  implicit def codeActionKind(kind: String): CodeActionKind = CodeActionKind.withName(kind)

  implicit def codeActionOptions(options: lsp4j.CodeActionOptions): CodeActionOptions =
    CodeActionOptions(Option(options.getCodeActionKinds).map(_.asScala.toSeq))

  implicit def eitherCodeActionProviderOptions(options: JEither[java.lang.Boolean, lsp4j.CodeActionOptions]): Option[CodeActionOptions] =
    either(options, booleanOrFalse, codeActionOptions)
      .fold(value => if (value) Some(CodeActionOptions()) else None, Some.apply)

  implicit def completionOptions(options: lsp4j.CompletionOptions): CompletionOptions =
    CompletionOptions(
      Option(options.getResolveProvider),
      Option(options.getTriggerCharacters).map(_.asScala.map(_(0)).toSet)
    )

  implicit def serverCapabilities(result: lsp4j.ServerCapabilities): ServerCapabilities =
    if (result == null) ServerCapabilities.empty
    else
      ServerCapabilities(
        Option(result.getTextDocumentSync).map(either(_, textDocumentSyncKind, textDocumentSyncOptions)),
        Option(result.getCompletionProvider).map(completionOptions),
        booleanOrFalse(result.getDefinitionProvider),
        booleanOrFalse(result.getReferencesProvider),
        booleanOrFalse(result.getDocumentSymbolProvider),
        Option(result.getRenameProvider).flatMap(eitherRenameOptions),
        Option(result.getCodeActionProvider).flatMap(eitherCodeActionProviderOptions),
        Option(result.getExperimental)
      )

  implicit def initializeResult(result: lsp4j.InitializeResult): InitializeResult =
    Option(result).map(r => InitializeResult(serverCapabilities(r.getCapabilities))).getOrElse(InitializeResult.empty)

  implicit def textDocumentIdentifier(identifier: lsp4j.TextDocumentIdentifier): TextDocumentIdentifier =
    TextDocumentIdentifier(identifier.getUri)

  implicit def textDocumentItem(item: lsp4j.TextDocumentItem): TextDocumentItem =
    TextDocumentItem(item.getUri, item.getLanguageId, item.getVersion, item.getText)

  implicit def position(position: lsp4j.Position): Position = Position(position.getLine, position.getCharacter)

  implicit def range(range: lsp4j.Range): Range = Range(range.getStart, range.getEnd)

  implicit def textDocumentContentChangeEvent(
      event: lsp4j.TextDocumentContentChangeEvent): TextDocumentContentChangeEvent =
    TextDocumentContentChangeEvent(event.getText, Option(event.getRange).map(range), Option(event.getRangeLength))

  implicit def versionedTextDocumentIdentifier(
      identifier: lsp4j.VersionedTextDocumentIdentifier): VersionedTextDocumentIdentifier =
    VersionedTextDocumentIdentifier(identifier.getUri, Option(identifier.getVersion))

  implicit def didOpenTextDocumentParams(params: lsp4j.DidOpenTextDocumentParams): DidOpenTextDocumentParams =
    DidOpenTextDocumentParams(params.getTextDocument)

  implicit def didCloseTextDocumentParams(params: lsp4j.DidCloseTextDocumentParams): DidCloseTextDocumentParams =
    DidCloseTextDocumentParams(params.getTextDocument)

  implicit def didChangeTextDocumentParams(params: lsp4j.DidChangeTextDocumentParams): DidChangeTextDocumentParams =
    DidChangeTextDocumentParams(params.getTextDocument,
                                params.getContentChanges.asScala.map(textDocumentContentChangeEvent))

  implicit def referenceContext(context: lsp4j.ReferenceContext): ReferenceContext =
    ReferenceContext(context.isIncludeDeclaration)

  implicit def completionTriggerKind(kind: lsp4j.CompletionTriggerKind): CompletionTriggerKind =
    CompletionTriggerKind(kind.getValue)

  implicit def completionContext(context: lsp4j.CompletionContext): CompletionContext =
    CompletionContext(context.getTriggerKind, Option(context.getTriggerCharacter).map(_(0)))

  implicit def referenceParams(params: lsp4j.ReferenceParams): ReferenceParams =
    ReferenceParams(params.getTextDocument, params.getPosition, params.getContext)

  implicit def textDocumentPositionParams(params: lsp4j.TextDocumentPositionParams): TextDocumentPositionParams =
    TextDocumentPositionParams(params.getTextDocument, params.getPosition)

  implicit def completionParams(params: lsp4j.CompletionParams): CompletionParams =
    CompletionParams(params.getTextDocument, params.getPosition, Option(params.getContext).map(completionContext))

  implicit def renameParams(params: lsp4j.RenameParams): RenameParams =
    RenameParams(params.getTextDocument, params.getPosition, params.getNewName)

  implicit def location(location: lsp4j.Location): Location =
    Location(location.getUri, location.getRange)

  implicit def diagnosticRelatedInformation(info: lsp4j.DiagnosticRelatedInformation): DiagnosticRelatedInformation =
    DiagnosticRelatedInformation(info.getLocation, info.getMessage)

  implicit def diagnosticSeverity(diagnosticSeverity: lsp4j.DiagnosticSeverity): DiagnosticSeverity =
    DiagnosticSeverity(diagnosticSeverity.getValue)

  implicit def diagnostic(diagnostic: lsp4j.Diagnostic): Diagnostic =
    Diagnostic(
      diagnostic.getRange,
      diagnostic.getMessage,
      Option(diagnostic.getSeverity).map(diagnosticSeverity),
      Option(diagnostic.getCode),
      Option(diagnostic.getSource),
      Option(diagnostic.getRelatedInformation).map(_.asScala.map(diagnosticRelatedInformation)).getOrElse(Seq())
    )

  implicit def codeActionContext(context: lsp4j.CodeActionContext): CodeActionContext =
    CodeActionContext(
      context.getDiagnostics.asScala.map(diagnostic),
      Option(context.getOnly).map(_.asScala.map(codeActionKind))
    )

  implicit def codeActionParams(params: lsp4j.CodeActionParams): CodeActionParams =
    CodeActionParams(params.getTextDocument, params.getRange, params.getContext)

  implicit def documentSymbolParams(params: lsp4j.DocumentSymbolParams): DocumentSymbolParams =
    DocumentSymbolParams(params.getTextDocument)

}
