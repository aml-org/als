package org.mulesoft.lsp

import java.util.{List => JList}

import org.eclipse.lsp4j
import org.eclipse.lsp4j.{
  DidChangeConfigurationCapabilities,
  DidChangeWatchedFilesCapabilities,
  ExecuteCommandCapabilities,
  SymbolCapabilities,
  WorkspaceEditCapabilities
}
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.lsp.feature.common.{
  Location,
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.configuration.TraceKind.TraceKind
import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{
  CodeActionContext,
  CodeActionKind,
  CodeActionOptions,
  CodeActionParams,
  CodeActionRegistrationOptions
}
import org.mulesoft.lsp.feature.completion.CompletionItemKind.CompletionItemKind
import org.mulesoft.lsp.feature.completion.CompletionTriggerKind.CompletionTriggerKind
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.definition.{DefinitionClientCapabilities, DefinitionParams}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity.DiagnosticSeverity
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.documentFormatting.{DocumentFormattingClientCapabilities, DocumentFormattingParams}
import org.mulesoft.lsp.feature.documentRangeFormatting.{
  DocumentRangeFormattingClientCapabilities,
  DocumentRangeFormattingParams
}
import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbolClientCapabilities,
  DocumentSymbolParams,
  SymbolKind,
  SymbolKindClientCapabilities
}
import org.mulesoft.lsp.feature.hover.{HoverClientCapabilities, HoverParams, MarkupKind}
import org.mulesoft.lsp.feature.folding.{FoldingRangeCapabilities, FoldingRangeParams}
import org.mulesoft.lsp.feature.highlight.{DocumentHighlightCapabilities, DocumentHighlightParams}
import org.mulesoft.lsp.feature.implementation.{ImplementationClientCapabilities, ImplementationParams}
import org.mulesoft.lsp.feature.link.{DocumentLinkClientCapabilities, DocumentLinkOptions, DocumentLinkParams}
import org.mulesoft.lsp.feature.reference.{ReferenceClientCapabilities, ReferenceContext, ReferenceParams}
import org.mulesoft.lsp.feature.rename.{PrepareRenameParams, RenameClientCapabilities, RenameOptions, RenameParams}
import org.mulesoft.lsp.feature.selectionRange.{SelectionRangeCapabilities, SelectionRangeParams}
import org.mulesoft.lsp.feature.typedefinition.{TypeDefinitionClientCapabilities, TypeDefinitionParams}
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object LspConversions {

  implicit def either[A, B, C, D](either: JEither[A, B], leftTo: A => C, rightTo: B => D): Either[C, D] =
    if (either.isLeft) Left(leftTo(either.getLeft)) else Right(rightTo(either.getRight))

  implicit def seq[A, B](list: JList[A], mapper: A => B): Seq[B] = list.asScala.map(mapper)

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

  implicit def documentFormattingClientCapabilities(
      capabilities: lsp4j.FormattingCapabilities): DocumentFormattingClientCapabilities =
    DocumentFormattingClientCapabilities(Some(capabilities.getDynamicRegistration))

  implicit def documentRangeFormattingClientCapabilities(
      capabilities: lsp4j.RangeFormattingCapabilities): DocumentRangeFormattingClientCapabilities =
    DocumentRangeFormattingClientCapabilities(Some(capabilities.getDynamicRegistration))

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

  implicit def implementationClientCapabilities(
      capabilities: lsp4j.ImplementationCapabilities): ImplementationClientCapabilities =
    ImplementationClientCapabilities(Option(capabilities.getDynamicRegistration), None)

  implicit def typeDefinitionClientCapabilities(
      capabilities: lsp4j.TypeDefinitionCapabilities): TypeDefinitionClientCapabilities =
    TypeDefinitionClientCapabilities(Option(capabilities.getDynamicRegistration), None)

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
      Option(capabilities.getImplementation).map(implementationClientCapabilities),
      Option(capabilities.getTypeDefinition).map(typeDefinitionClientCapabilities),
      Option(capabilities.getRename).map(renameClientCapabilities),
      Option(capabilities.getCodeAction).flatMap(_ => None), // TODO: CodeAction
      Option(capabilities.getDocumentLink).map(documentLinkClientCapabilities),
      Option(capabilities.getHover).map(clientHoverCapabilities),
      Option(capabilities.getDocumentHighlight).map(documentHighlightCapabilities),
      Option(capabilities.getFoldingRange).map(foldingRangeCapabilities),
      Option(capabilities.getSelectionRange).map(selectionRangeCapabilities),
      Option(capabilities.getFormatting).map(documentFormattingClientCapabilities),
      Option(capabilities.getRangeFormatting).map(documentRangeFormattingClientCapabilities)
    )

  def workspaceEditClientCapabilities(c: WorkspaceEditCapabilities): WorkspaceEditClientCapabilities =
    WorkspaceEditClientCapabilities(Option(c.getDocumentChanges))

  def workspaceDidChangeConfiguration(
      c: DidChangeConfigurationCapabilities): DidChangeConfigurationClientCapabilities =
    DidChangeConfigurationClientCapabilities(Option(c.getDynamicRegistration))

  def workspaceDidChangeWatchedFiles(c: DidChangeWatchedFilesCapabilities): DidChangeWatchedFilesClientCapabilities =
    DidChangeWatchedFilesClientCapabilities(Option(c.getDynamicRegistration))

  def workspaceSymbol(c: SymbolCapabilities): WorkspaceSymbolClientCapabilities =
    WorkspaceSymbolClientCapabilities(Option(c.getDynamicRegistration))

  def workspaceExecuteCommand(c: ExecuteCommandCapabilities): ExecuteCommandClientCapabilities =
    ExecuteCommandClientCapabilities(Option(c.getDynamicRegistration))

  implicit def workspaceClientCapabilities(
      capabilities: lsp4j.WorkspaceClientCapabilities): WorkspaceClientCapabilities = {
    Option(capabilities)
      .map(
        c =>
          WorkspaceClientCapabilities(
            Option(c.getApplyEdit),
            Option(c.getWorkspaceEdit).map(workspaceEditClientCapabilities),
            Option(c.getDidChangeConfiguration).map(workspaceDidChangeConfiguration),
            Option(c.getDidChangeWatchedFiles).map(workspaceDidChangeWatchedFiles),
            Option(c.getSymbol).map(workspaceSymbol),
            Option(c.getExecuteCommand).map(workspaceExecuteCommand)
        ))
      .getOrElse(WorkspaceClientCapabilities())
  }

  implicit def traceKind(kind: String): TraceKind = TraceKind.withName(kind)

  implicit def workspaceFolder(folder: lsp4j.WorkspaceFolder): WorkspaceFolder =
    if (folder == null) WorkspaceFolder(None, None) else WorkspaceFolder(Option(folder.getUri), Option(folder.getName))

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
    CodeActionRegistrationOptions(
      Option(options.getCodeActionKinds)
        .map(kinds =>
          kinds.asScala
            .map(kind =>
              try {
                CodeActionKind.withName(kind)
              } catch {
                case _: NoSuchElementException =>
                  CodeActionKind.Empty
            })))

  implicit def staticRegistrationOptions(options: lsp4j.StaticRegistrationOptions): StaticRegistrationOptions =
    StaticRegistrationOptions(Option(options.getId))

  implicit def eitherCodeActionProviderOptions(
      options: JEither[java.lang.Boolean, lsp4j.CodeActionOptions]): Option[CodeActionOptions] =
    either(options, booleanOrFalse, codeActionOptions)
      .fold(value => if (value) Some(CodeActionRegistrationOptions()) else None, Some.apply)

  implicit def completionOptions(options: lsp4j.CompletionOptions): CompletionOptions =
    CompletionOptions(
      Option(options.getResolveProvider),
      Option(options.getTriggerCharacters).map(_.asScala.map(_(0)).toSet)
    )

  implicit def workspaceFolderServerCapabilities(
      options: lsp4j.WorkspaceFoldersOptions): WorkspaceFolderServerCapabilities =
    WorkspaceFolderServerCapabilities(
      Option(options.getSupported),
      Option(either(options.getChangeNotifications, (a: String) => a, (a: java.lang.Boolean) => a)))

  implicit def workspaceServerCapabilities(
      capabilities: lsp4j.WorkspaceServerCapabilities): WorkspaceServerCapabilities =
    WorkspaceServerCapabilities(Option(capabilities.getWorkspaceFolders))

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
    CompletionContext(context.getTriggerKind, context.getTriggerCharacter.lift(0))

  implicit def referenceParams(params: lsp4j.ReferenceParams): ReferenceParams =
    ReferenceParams(params.getTextDocument, params.getPosition, params.getContext)

  implicit def textDocumentPositionParams(params: lsp4j.TextDocumentPositionParams): TextDocumentPositionParams =
    TextDocumentPositionParams(params.getTextDocument, params.getPosition)

  implicit def definitionParams(params: lsp4j.DefinitionParams): DefinitionParams =
    DefinitionParams(params.getTextDocument, params.getPosition)

  implicit def typeDefinitionParams(params: lsp4j.TypeDefinitionParams): TypeDefinitionParams =
    TypeDefinitionParams(params.getTextDocument, params.getPosition)

  implicit def implementationParams(params: lsp4j.ImplementationParams): ImplementationParams =
    ImplementationParams(params.getTextDocument, params.getPosition)

  implicit def hoverParams(params: lsp4j.HoverParams): HoverParams =
    HoverParams(params.getTextDocument, params.getPosition)

  implicit def clientHoverCapabilities(params: lsp4j.HoverCapabilities): HoverClientCapabilities =
    HoverClientCapabilities(
      Option(params.getDynamicRegistration),
      Option(params.getContentFormat).map(_.asScala.map(c => MarkupKind.withName(c))).getOrElse(Nil))

  implicit def completionParams(params: lsp4j.CompletionParams): CompletionParams =
    CompletionParams(params.getTextDocument, params.getPosition, Option(params.getContext).map(completionContext))

  implicit def renameParams(params: lsp4j.RenameParams): RenameParams =
    RenameParams(params.getTextDocument, params.getPosition, params.getNewName)

  implicit def prepareRenameParams(params: lsp4j.PrepareRenameParams): PrepareRenameParams =
    PrepareRenameParams(params.getTextDocument, params.getPosition)

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
      Option(diagnostic.getCode).flatMap(c => either(c, (s: String) => s, (n: Number) => n.toString).toOption),
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

  implicit def documentLinkParams(params: lsp4j.DocumentLinkParams): DocumentLinkParams =
    DocumentLinkParams(params.getTextDocument)

  implicit def documentLinkClientCapabilities(
      capabilities: lsp4j.DocumentLinkCapabilities): DocumentLinkClientCapabilities =
    DocumentLinkClientCapabilities(Option(capabilities.getDynamicRegistration), None)

  implicit def documentLinkOptions(options: lsp4j.DocumentLinkOptions): DocumentLinkOptions =
    DocumentLinkOptions(Option(options.getResolveProvider))

  implicit def documentHighlightCapabilities(
      capa: lsp4j.DocumentHighlightCapabilities): DocumentHighlightCapabilities =
    DocumentHighlightCapabilities(Option(capa.getDynamicRegistration))

  implicit def documentHighlightParams(inner: lsp4j.DocumentHighlightParams): DocumentHighlightParams =
    DocumentHighlightParams(inner.getTextDocument, inner.getPosition)

  implicit def foldingRangeCapabilities(capa: lsp4j.FoldingRangeCapabilities): FoldingRangeCapabilities =
    FoldingRangeCapabilities(Option(capa.getDynamicRegistration),
                             Option(capa.getRangeLimit),
                             Option(capa.getLineFoldingOnly))

  implicit def foldingRangeParams(inner: lsp4j.FoldingRangeRequestParams): FoldingRangeParams =
    FoldingRangeParams(inner.getTextDocument)

  implicit def selectionRangeCapabilities(c: lsp4j.SelectionRangeCapabilities): SelectionRangeCapabilities =
    SelectionRangeCapabilities(Option(c.getDynamicRegistration))

  implicit def selectionRangeParams(p: lsp4j.SelectionRangeParams): SelectionRangeParams =
    SelectionRangeParams(p.getTextDocument, seq(p.getPositions, position))

  implicit def formattingOptions(o: lsp4j.FormattingOptions): FormattingOptions =
    FormattingOptions(o.getTabSize, o.isInsertSpaces)

  implicit def documentFormattingParams(p: lsp4j.DocumentFormattingParams): DocumentFormattingParams =
    DocumentFormattingParams(p.getTextDocument, p.getOptions)

  implicit def documentRangeFormattingParams(p: lsp4j.DocumentRangeFormattingParams): DocumentRangeFormattingParams =
    DocumentRangeFormattingParams(p.getTextDocument, p.getRange, formattingOptions(p.getOptions))
}
