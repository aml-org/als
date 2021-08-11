package org.mulesoft.lsp

import org.eclipse.lsp4j
import org.eclipse.lsp4j.MarkedString
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.lsp.configuration.{StaticRegistrationOptions, WorkDoneProgressOptions}
import org.mulesoft.lsp.edit._
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind, CodeActionOptions}
import org.mulesoft.lsp.feature.command.Command
import org.mulesoft.lsp.feature.common.{Location, LocationLink, Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.completion.CompletionItemKind.CompletionItemKind
import org.mulesoft.lsp.feature.completion.InsertTextFormat.InsertTextFormat
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionList, CompletionOptions}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity.DiagnosticSeverity
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation, PublishDiagnosticsParams}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbol, SymbolInformation}
import org.mulesoft.lsp.feature.folding.FoldingRangeKind.FoldingRangeKind
import org.mulesoft.lsp.feature.folding.{FoldingRange, FoldingRangeKind}
import org.mulesoft.lsp.feature.highlight.DocumentHighlight
import org.mulesoft.lsp.feature.highlight.DocumentHighlightKind.DocumentHighlightKind
import org.mulesoft.lsp.feature.hover.Hover
import org.mulesoft.lsp.feature.link.{DocumentLink, DocumentLinkOptions, DocumentLinkParams}
import org.mulesoft.lsp.feature.rename.{PrepareRenameResult, RenameOptions}
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync.{SaveOptions, TextDocumentSyncKind, TextDocumentSyncOptions}

import java.util
import java.util.concurrent.CompletableFuture
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

object Lsp4JConversions {

  private implicit def javaInteger(option: Option[Int]): Integer = option.map(Integer.valueOf).orNull

  private implicit def javaBoolean(option: Option[Boolean]): java.lang.Boolean =
    option.map(java.lang.Boolean.valueOf).orNull

  def jEither[A, B, C, D](either: Either[A, B], leftTo: A => C, rightTo: B => D): JEither[C, D] =
    either.fold[JEither[C, D]](a => JEither.forLeft[C, D](leftTo(a)), b => JEither.forRight[C, D](rightTo(b)))

  private def javaList[F, T](items: Seq[F], convert: F => T): util.List[T] = items.map(convert).toList.asJava

  implicit def javaFuture[F, T](future: Future[F], convert: F => T)(
      implicit context: ExecutionContext): CompletableFuture[T] =
    future.map[T](convert).toJava.toCompletableFuture

  implicit def lsp4JTextEdit(textEdit: TextEdit): lsp4j.TextEdit =
    new lsp4j.TextEdit(textEdit.range, textEdit.newText)

  implicit def lsp4JInsertReplaceEdit(insertReplaceEdit: InsertReplaceEdit): lsp4j.InsertReplaceEdit =
    new lsp4j.InsertReplaceEdit(insertReplaceEdit.newText, insertReplaceEdit.insert, insertReplaceEdit.replace)

  implicit def lsp4JTextEdits(textEdits: Seq[TextEdit]): util.List[lsp4j.TextEdit] =
    javaList(textEdits, lsp4JTextEdit)

  implicit def lsp4JVersionedTextDocumentIdentifier(
      identifier: VersionedTextDocumentIdentifier): lsp4j.VersionedTextDocumentIdentifier =
    new lsp4j.VersionedTextDocumentIdentifier(identifier.uri, identifier.version)

  implicit def lsp4JTextDocumentEdit(textEdit: TextDocumentEdit): lsp4j.TextDocumentEdit =
    new lsp4j.TextDocumentEdit(textEdit.textDocument, textEdit.edits)

  implicit def lsp4JCreateFileOptions(options: NewFileOptions): lsp4j.CreateFileOptions =
    new lsp4j.CreateFileOptions(options.overwrite, options.ignoreIfExists)

  implicit def lsp4JRenameFileOptions(options: NewFileOptions): lsp4j.RenameFileOptions =
    new lsp4j.RenameFileOptions(options.overwrite, options.ignoreIfExists)

  implicit def lsp4JRenameFile(renameFile: RenameFile): lsp4j.RenameFile =
    new lsp4j.RenameFile(renameFile.oldUri, renameFile.newUri, renameFile.options.map(lsp4JRenameFileOptions).orNull)

  implicit def lsp4JDeleteFileOptions(options: DeleteFileOptions): lsp4j.DeleteFileOptions =
    new lsp4j.DeleteFileOptions(options.recursive, options.ignoreIfNotExists)

  implicit def lsp4JResourceOperation(operation: ResourceOperation): lsp4j.ResourceOperation = operation match {
    case CreateFile(uri, options) => new lsp4j.CreateFile(uri, options.map(lsp4JCreateFileOptions).orNull)
    case RenameFile(uri, newUri, options) =>
      new lsp4j.RenameFile(uri, newUri, options.map(lsp4JRenameFileOptions).orNull)
    case DeleteFile(uri, options) => new lsp4j.DeleteFile(uri, options.map(lsp4JDeleteFileOptions).orNull)
  }

  implicit def lsp4JWorkspaceEdit(workspaceEdit: WorkspaceEdit): lsp4j.WorkspaceEdit = {
    val result = new lsp4j.WorkspaceEdit()

    result.setChanges(workspaceEdit.changes.map(_.mapValues(_.map(lsp4JTextEdit).asJava).asJava).orNull)
    result.setDocumentChanges(
      workspaceEdit.documentChanges
        .map(_.map {
          case Left(edit) =>
            JEither.forLeft(lsp4JTextDocumentEdit(edit)): JEither[lsp4j.TextDocumentEdit, lsp4j.ResourceOperation]
          case Right(operation) =>
            JEither
              .forRight(lsp4JResourceOperation(operation)): JEither[lsp4j.TextDocumentEdit, lsp4j.ResourceOperation]
        }.asJava)
        .orNull)

    result
  }

  implicit def lsp4JPosition(position: Position): lsp4j.Position =
    new lsp4j.Position(position.line, position.character)

  implicit def lsp4JRange(range: Range): lsp4j.Range =
    new lsp4j.Range(lsp4JPosition(range.start), range.end)

  implicit def lsp4JLocation(location: Location): lsp4j.Location =
    new lsp4j.Location(location.uri, location.range)

  implicit def lsp4JLocationLink(locationLink: LocationLink): lsp4j.LocationLink =
    new lsp4j.LocationLink(locationLink.targetUri,
                           locationLink.targetRange,
                           locationLink.targetSelectionRange,
                           locationLink.originSelectionRange.map(lsp4JRange).orNull)

  implicit def lsp4JLocations(locations: Seq[Location]): util.List[lsp4j.Location] =
    javaList(locations, lsp4JLocation)

  implicit def lsp4JLocationLinks(locationLinks: Seq[LocationLink]): util.List[lsp4j.LocationLink] =
    javaList(locationLinks, lsp4JLocationLink)

  implicit def lsp4JCompletionItemKind(kind: CompletionItemKind): lsp4j.CompletionItemKind =
    lsp4j.CompletionItemKind.forValue(kind.id)

  implicit def lsp4JInsertTextFormat(kind: InsertTextFormat): lsp4j.InsertTextFormat =
    lsp4j.InsertTextFormat.forValue(kind.id)

  implicit def lsp4JCommand(command: Command): lsp4j.Command =
    new lsp4j.Command(command.title, command.command)

  implicit def lsp4JCompletionItem(item: CompletionItem): lsp4j.CompletionItem = {
    val result = new lsp4j.CompletionItem(item.label)

    result.setKind(item.kind.map(lsp4JCompletionItemKind).orNull)
    result.setDetail(item.detail.orNull)
    result.setDocumentation(item.documentation.orNull)
    result.setDeprecated(item.deprecated)
    result.setPreselect(item.preselect)
    result.setSortText(item.sortText.orNull)
    result.setFilterText(item.filterText.orNull)
    result.setInsertText(item.insertText.orNull)
    result.setInsertTextFormat(item.insertTextFormat.map(lsp4JInsertTextFormat).orNull)
    result.setTextEdit(item.textEdit.map(lsp4JTextEditEither).orNull)
    result.setAdditionalTextEdits(item.additionalTextEdits.map(javaList(_, lsp4JTextEdit)).orNull)
    result.setCommitCharacters(item.commitCharacters.map(javaList[Char, String](_, String.valueOf)).orNull)
    result.setCommand(item.command.map(lsp4JCommand).orNull)

    result
  }

  implicit def lsp4JCompletionItems(items: Seq[CompletionItem]): util.List[lsp4j.CompletionItem] =
    javaList(items, lsp4JCompletionItem)

  implicit def lsp4JCompletionList(list: CompletionList): lsp4j.CompletionList =
    new lsp4j.CompletionList(list.isIncomplete, javaList(list.items, lsp4JCompletionItem))

  implicit def lsp4JCompletionEither(either: Either[Seq[CompletionItem], CompletionList])
    : JEither[util.List[lsp4j.CompletionItem], lsp4j.CompletionList] =
    jEither(either, lsp4JCompletionItems, lsp4JCompletionList)

  implicit def lsp4JTextEditEither(
      either: Either[TextEdit, InsertReplaceEdit]): JEither[lsp4j.TextEdit, lsp4j.InsertReplaceEdit] =
    jEither(either, lsp4JTextEdit, lsp4JInsertReplaceEdit)

  implicit def lsp4JLocationsEither(either: Either[Seq[_ <: Location], Seq[_ <: LocationLink]])
    : JEither[util.List[_ <: lsp4j.Location], util.List[_ <: lsp4j.LocationLink]] =
    jEither(either, lsp4JLocations, lsp4JLocationLinks)

  implicit def lsp4JDocumentSymbol(symbol: DocumentSymbol): lsp4j.DocumentSymbol = {
    val result =
      new lsp4j.DocumentSymbol(symbol.name,
                               lsp4j.SymbolKind.forValue(symbol.kind.id),
                               symbol.range,
                               symbol.selectionRange)

    result.setDeprecated(symbol.deprecated)
    result.setChildren(javaList(symbol.children, lsp4JDocumentSymbol))

    result
  }

  implicit def lsp4JSymbolInformation(symbol: SymbolInformation): lsp4j.SymbolInformation = {
    val result = new lsp4j.SymbolInformation(symbol.name,
                                             lsp4j.SymbolKind.forValue(symbol.kind.id),
                                             symbol.location,
                                             symbol.containerName.orNull)

    result.setDeprecated(symbol.deprecated)

    result
  }

  implicit def lsp4JDocumentSymbols(symbols: Seq[DocumentSymbol]): util.List[lsp4j.DocumentSymbol] =
    javaList(symbols, lsp4JDocumentSymbol)

  implicit def lsp4JCodeAction(codeAction: CodeAction): lsp4j.CodeAction = {
    val result = new lsp4j.CodeAction(codeAction.title)

    result.setKind(codeAction.kind.map(lsp4JCodeActionKind).orNull)
    result.setCommand(codeAction.command.map(lsp4JCommand).orNull)
    result.setEdit(codeAction.edit.map(lsp4JWorkspaceEdit).orNull)
    codeAction.diagnostics
      .foreach(diagnostics => result.setDiagnostics(javaList(diagnostics, lsp4JDiagnostic)))

    result
  }

  implicit def lsp4JCodeActionResult(result: Seq[CodeAction]): util.List[JEither[lsp4j.Command, lsp4j.CodeAction]] =
    javaList(result, (action: CodeAction) => JEither.forRight(lsp4JCodeAction(action)))

  implicit def lsp4JDocumentLinkRequestResult(result: Seq[DocumentLink]): util.List[lsp4j.DocumentLink] =
    javaList(result, (link: DocumentLink) => lsp4JDocumentLink(link))

  implicit def lsp4JDocumentLink(documentLink: DocumentLink): lsp4j.DocumentLink =
    new lsp4j.DocumentLink(documentLink.range, documentLink.target, documentLink.data)

  implicit def lsp4JDocumentSymbolsResult(result: Either[Seq[SymbolInformation], Seq[DocumentSymbol]])
    : util.List[JEither[lsp4j.SymbolInformation, lsp4j.DocumentSymbol]] =
    result.fold[util.List[JEither[lsp4j.SymbolInformation, lsp4j.DocumentSymbol]]](
      results =>
        javaList(results,
                 (item: SymbolInformation) => JEither.forLeft[lsp4j.SymbolInformation, lsp4j.DocumentSymbol](item)),
      results =>
        javaList(results,
                 (item: DocumentSymbol) => JEither.forRight[lsp4j.SymbolInformation, lsp4j.DocumentSymbol](item))
    )

  implicit def lsp4JTextDocumentSyncKind(kind: TextDocumentSyncKind): lsp4j.TextDocumentSyncKind = kind match {
    case TextDocumentSyncKind.Full        => lsp4j.TextDocumentSyncKind.Full
    case TextDocumentSyncKind.Incremental => lsp4j.TextDocumentSyncKind.Incremental
    case TextDocumentSyncKind.None        => lsp4j.TextDocumentSyncKind.None
  }

  implicit def lsp4JSaveOptions(options: SaveOptions): lsp4j.SaveOptions =
    new lsp4j.SaveOptions(options.includeText)

  implicit def lsp4JTextDocumentSyncOptions(options: TextDocumentSyncOptions): lsp4j.TextDocumentSyncOptions = {
    val result = new lsp4j.TextDocumentSyncOptions()

    result.setOpenClose(options.openClose)
    result.setChange(options.change.map(lsp4JTextDocumentSyncKind).orNull)
    result.setWillSave(options.willSave)
    result.setWillSaveWaitUntil(options.willSaveWaitUntil)
    result.setSave(options.save.map(lsp4JSaveOptions).orNull)

    result
  }

  implicit def lsp4JRenameOptions(options: RenameOptions): lsp4j.RenameOptions = {
    val result = new lsp4j.RenameOptions()

    result.setPrepareProvider(options.prepareProvider)

    result
  }

  implicit def lsp4JOptionEitherRangeWithPlaceholder(
      p: Option[Either[Range, PrepareRenameResult]]): JEither[lsp4j.Range, lsp4j.PrepareRenameResult] =
    p.map {
      case Left(r) =>
        JEither.forLeft(lsp4JRange(r)): JEither[lsp4j.Range, lsp4j.PrepareRenameResult]
      case Right(r) =>
        JEither.forRight(new lsp4j.PrepareRenameResult(lsp4JRange(r.range), r.placeholder)): JEither[
          lsp4j.Range,
          lsp4j.PrepareRenameResult]
    }.orNull

  implicit def lsp4JStaticRegistrationOptions(options: StaticRegistrationOptions): lsp4j.StaticRegistrationOptions = {
    val result = new lsp4j.StaticRegistrationOptions()
    options.id.foreach(result.setId)
    result
  }

  implicit def lsp4JEitherRenameOptions(
      options: Option[RenameOptions]): JEither[java.lang.Boolean, lsp4j.RenameOptions] =
    options
      .map(renameOptions =>
        JEither.forRight[java.lang.Boolean, lsp4j.RenameOptions](lsp4JRenameOptions(renameOptions)))
      .getOrElse(JEither.forLeft(false))

  implicit def lsp4JEitherStaticregistrationOptions(options: Option[Either[Boolean, StaticRegistrationOptions]])
    : JEither[java.lang.Boolean, lsp4j.StaticRegistrationOptions] =
    options
      .map {
        case Right(implementationOptions) =>
          JEither.forRight[java.lang.Boolean, lsp4j.StaticRegistrationOptions](
            lsp4JStaticRegistrationOptions(implementationOptions))
        case Left(bool) =>
          JEither.forLeft[java.lang.Boolean, lsp4j.StaticRegistrationOptions](bool)
      }
      .getOrElse(JEither.forLeft(false))

  implicit def lsp4JEitherWorkDoneProgressOptions[T <: lsp4j.WorkDoneProgressOptions](
      options: Option[Either[Boolean, WorkDoneProgressOptions]])(
      toLsp4J: WorkDoneProgressOptions => T): JEither[java.lang.Boolean, T] = {
    options
      .map {
        case Right(implementationOptions) =>
          JEither.forRight[java.lang.Boolean, T](toLsp4J(implementationOptions))
        case Left(bool) =>
          JEither.forLeft[java.lang.Boolean, T](bool)
      }
      .getOrElse(JEither.forLeft(false))
  }

  implicit def lsp4JCompletionOptions(options: CompletionOptions): lsp4j.CompletionOptions =
    new lsp4j.CompletionOptions(
      options.resolveProvider,
      options.triggerCharacters.map(_.map(java.lang.String.valueOf).toList.asJava).orNull
    )

  implicit def lsp4JCodeActionKind(kind: CodeActionKind): String = kind match {
    case CodeActionKind.QuickFix              => lsp4j.CodeActionKind.QuickFix
    case CodeActionKind.Refactor              => lsp4j.CodeActionKind.Refactor
    case CodeActionKind.RefactorExtract       => lsp4j.CodeActionKind.RefactorExtract
    case CodeActionKind.RefactorInline        => lsp4j.CodeActionKind.RefactorInline
    case CodeActionKind.RefactorRewrite       => lsp4j.CodeActionKind.RefactorRewrite
    case CodeActionKind.Source                => lsp4j.CodeActionKind.Source
    case CodeActionKind.SourceOrganizeImports => lsp4j.CodeActionKind.SourceOrganizeImports
    case _                                    => kind.toString
  }

  implicit def lsp4JCodeActionOptions(options: CodeActionOptions): lsp4j.CodeActionOptions =
    new lsp4j.CodeActionOptions(options.codeActionKinds.map(list => list.map(_.toString).asJava).orNull)

  implicit def lsp4JEitherCodeActionOptions(
      options: Option[CodeActionOptions]): JEither[java.lang.Boolean, lsp4j.CodeActionOptions] =
    options
      .map(codeActionOptions =>
        JEither.forRight[java.lang.Boolean, lsp4j.CodeActionOptions](lsp4JCodeActionOptions(codeActionOptions)))
      .getOrElse(JEither.forLeft(false))

  implicit def lsp4JDiagnosticSeverity(diagnostic: DiagnosticSeverity): lsp4j.DiagnosticSeverity =
    lsp4j.DiagnosticSeverity.forValue(diagnostic.id)

  implicit def lsp4JDiagnosticRelatedInformation(
      diagnostic: DiagnosticRelatedInformation): lsp4j.DiagnosticRelatedInformation =
    new lsp4j.DiagnosticRelatedInformation(diagnostic.location, diagnostic.message)

  implicit def lsp4JDiagnostic(diagnostic: Diagnostic): lsp4j.Diagnostic = {
    val result = new lsp4j.Diagnostic(
      diagnostic.range,
      diagnostic.message,
      diagnostic.severity.map(lsp4JDiagnosticSeverity).orNull,
      diagnostic.source.orNull,
      diagnostic.code.orNull
    )

    result.setRelatedInformation(javaList(diagnostic.relatedInformation, lsp4JDiagnosticRelatedInformation))
    diagnostic.codeDescription.foreach(desc => result.setCodeDescription(new lsp4j.DiagnosticCodeDescription(desc)))
    result
  }

  implicit def lsp4JPublishDiagnosticsParams(params: PublishDiagnosticsParams): lsp4j.PublishDiagnosticsParams =
    new lsp4j.PublishDiagnosticsParams(params.uri, javaList(params.diagnostics, lsp4JDiagnostic))

  implicit def lsp4JPublishDiagnosticsParamsSeq(
      seq: Seq[PublishDiagnosticsParams]): util.List[lsp4j.PublishDiagnosticsParams] =
    javaList(seq, lsp4JPublishDiagnosticsParams)
  implicit def lsp4JDocumentLinkParams(params: DocumentLinkParams): lsp4j.DocumentLinkParams =
    new lsp4j.DocumentLinkParams(new lsp4j.TextDocumentIdentifier(params.textDocument.uri))

  implicit def lsp4JDocumentLinkOptions(options: DocumentLinkOptions): lsp4j.DocumentLinkOptions =
    new lsp4j.DocumentLinkOptions(options.resolveProvider)

  implicit def lsp4JDocumentHighlight(dh: DocumentHighlight): lsp4j.DocumentHighlight =
    new lsp4j.DocumentHighlight(dh.range, dh.kind)

  implicit def lsp4JSelectionRanges(items: Seq[SelectionRange]): util.List[lsp4j.SelectionRange] =
    javaList(items, lsp4JSelectionRange)

  implicit def lsp4JSelectionRange(sr: SelectionRange): lsp4j.SelectionRange = {
    new lsp4j.SelectionRange(sr.range, sr.parent.map(lsp4JSelectionRange).orNull)
  }

  implicit def lsp4JDocumentHighlightKind(dhk: DocumentHighlightKind): lsp4j.DocumentHighlightKind =
    dhk.id match {
      case 1 => lsp4j.DocumentHighlightKind.Text
      case 2 => lsp4j.DocumentHighlightKind.Read
      case 3 => lsp4j.DocumentHighlightKind.Write
    }

  implicit def lsp4JDocumentHighlights(items: Seq[DocumentHighlight]): util.List[lsp4j.DocumentHighlight] =
    javaList(items, lsp4JDocumentHighlight)

  implicit def lsp4JHoverParams(hover: Hover): lsp4j.Hover = {
    val java: util.List[JEither[String, lsp4j.MarkedString]] =
      hover.contents.map(c => JEither.forLeft[String, MarkedString](c)).asJava
    hover.range match {
      case Some(r) => new lsp4j.Hover(java, r)
      case _       => new lsp4j.Hover(java)
    }
  }
  implicit def lsp4JFoldingRange(dh: FoldingRange): lsp4j.FoldingRange = {
    val range = new lsp4j.FoldingRange(dh.startLine, dh.endLine)
    dh.startCharacter.foreach(c => range.setStartCharacter(c))
    dh.endCharacter.foreach(c => range.setEndCharacter(c))
    dh.kind.foreach(c => range.setKind(lsp4JFoldingRangeKind(c)))
    range
  }

  implicit def lsp4JFoldingRangeKind(dhk: FoldingRangeKind): String = // redundant, but to avoid any inconsistencies on the client side
    dhk match {
      case FoldingRangeKind.Comment => lsp4j.FoldingRangeKind.Comment
      case FoldingRangeKind.Imports => lsp4j.FoldingRangeKind.Imports
      case FoldingRangeKind.Region  => lsp4j.FoldingRangeKind.Region
    }

  implicit def lsp4JFoldingRanges(items: Seq[FoldingRange]): util.List[lsp4j.FoldingRange] =
    javaList(items, lsp4JFoldingRange)
}
