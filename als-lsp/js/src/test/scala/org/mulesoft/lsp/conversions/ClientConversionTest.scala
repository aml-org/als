package org.mulesoft.lsp.conversions

import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.convert.LspConvertersClientToShared._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
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
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingClientCapabilities
import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingClientCapabilities
import org.mulesoft.lsp.feature.documentsymbol._
import org.mulesoft.lsp.feature.link._
import org.mulesoft.lsp.feature.reference._
import org.mulesoft.lsp.feature.rename._
import org.mulesoft.lsp.feature.telemetry.{
  ClientTelemetryClientCapabilities,
  ClientTelemetryMessage,
  TelemetryClientCapabilities,
  TelemetryMessage
}
import org.mulesoft.lsp.textsync.KnownDependencyScopes._
import org.mulesoft.lsp.textsync._
import org.mulesoft.lsp.workspace._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.scalajs.js.JSON

class ClientConversionTest extends AnyFlatSpec with Matchers {

  behavior of "Common transformations"
  val p: Position = Position(10, 10)
  val r: Range    = Range(p, p)

  it should "transform positions" in {
    val pStringified       = "{\"line\":10,\"character\":10}"
    val p1: ClientPosition = p.toClient
    val p2: Position       = p1.toShared

    JSON.stringify(p1) should be(pStringified)

    p should be(p2)
  }

  it should "transform ranges" in {
    val rStringified    = "{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}"
    val r1: ClientRange = r.toClient
    val r2: Range       = r1.toShared

    JSON.stringify(r1) should be(rStringified)

    r should be(r2)
  }

  it should "transform LocationLink" in {
    val llStringified =
      "{\"targetUri\":\"uri\",\"targetRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"targetSelectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"originSelectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}}"
    val ll: LocationLink        = LocationLink("uri", r, r, Some(r))
    val ll1: ClientLocationLink = ll.toClient
    val ll2: LocationLink       = ll1.toShared

    JSON.stringify(ll1) should be(llStringified)

    ll should be(ll2)
  }

  val tdi: TextDocumentIdentifier = TextDocumentIdentifier("uri")

  it should "transform TextDocumentIdentifier" in {
    val tdiStringified                     = "{\"uri\":\"uri\"}"
    val tdi1: ClientTextDocumentIdentifier = tdi.toClient
    val tdi2: TextDocumentIdentifier       = tdi1.toShared

    JSON.stringify(tdi1) should be(tdiStringified)

    tdi should be(tdi2)
  }

  val vtdi: VersionedTextDocumentIdentifier = VersionedTextDocumentIdentifier("uri", Some(1))
  it should "transform VersionedTextDocumentIdentifier" in {
    val vtdiStringified                              = "{\"uri\":\"uri\",\"version\":1}"
    val vtdi1: ClientVersionedTextDocumentIdentifier = vtdi.toClient
    val vtdi2: VersionedTextDocumentIdentifier       = vtdi1.toShared

    JSON.stringify(vtdi1) should be(vtdiStringified)

    vtdi should be(vtdi2)
  }

  val tdItem: TextDocumentItem = TextDocumentItem("uri", "test", 1, "test text")
  it should "transform TextDocumentItem" in {
    val tdiStringified               = "{\"uri\":\"uri\",\"languageId\":\"test\",\"version\":1,\"text\":\"test text\"}"
    val tdi1: ClientTextDocumentItem = tdItem.toClient
    val tdi2: TextDocumentItem       = tdi1.toShared

    JSON.stringify(tdi1) should be(tdiStringified)
    tdItem should be(tdi2)
  }

  it should "transform TextDocumentPositionParams" in {
    val tdiStringified = "{\"textDocument\":{\"uri\":\"uri\"},\"position\":{\"line\":10,\"character\":10}}"
    val tdpp: TextDocumentPositionParams        = TextDocumentPositionParams(tdi, p)
    val tdpp1: ClientTextDocumentPositionParams = tdpp.toClient
    val tdpp2: TextDocumentPositionParams       = tdpp1.toShared

    JSON.stringify(tdpp1) should be(tdiStringified)
    tdpp.position should be(tdpp2.position)
    tdpp.textDocument should be(tdpp2.textDocument)
  }

  val l: Location = Location("uri", r)
  it should "transform Location" in {
    val lStringified =
      "{\"uri\":\"uri\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}}"
    val l1: ClientLocation = l.toClient
    val l2: Location       = l1.toShared

    JSON.stringify(l1) should be(lStringified)
    l should be(l2)
  }

  // end common

  // todo: command should transform arguments recursive somehow
  behavior of "Command transformations"
  ignore should "transform Command" in {
    val comTitle = "test title"
    val comTest  = "test command"

    val stringified =
      "{\"title\":\"test title\",\"command\":\"test command\",\"arguments\":[\"arg1\",{\"title\":\"s\",\"command\":\"s\",\"arguments\":{}}]}"
    val c: Command        = Command(comTitle, comTest, Some(Seq("arg1", Command("s", "s", None))))
    val c1: ClientCommand = c.toClient
    val c2: Command       = c1.toShared

    JSON.stringify(c1) should be(stringified)

    c should be(c2)
  }

  // end command

  behavior of "Configuration transformations"

  it should "transform StaticRegistrationOptions" in {
    val sro  = StaticRegistrationOptions(Some("id"))
    val sro1 = sro.toClient
    val sro2 = sro1.toShared

    val stringified = "{\"id\":\"id\"}"

    JSON.stringify(sro1) should be(stringified)

    sro should be(sro2)
  }

  val wf = WorkspaceFolder(Some("uri"), Some("name"))
  it should "transform WorkspaceFolder" in {
    val wf1 = wf.toClient
    val wf2 = wf1.toShared

    val stringified = "{\"uri\":\"uri\",\"name\":\"name\"}"

    JSON.stringify(wf1) should be(stringified)

    wf should be(wf2)
  }

  // end of Configuration

  behavior of "Edit transformations"
  val te = TextEdit(r, "text")

  it should "transform TextEdit" in {
    val te1 = te.toClient
    val te2 = te1.toShared

    val stringified =
      "{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}"

    JSON.stringify(te1) should be(stringified)

    te should be(te2)
  }

  val tde = TextDocumentEdit(vtdi, Seq(te))
  it should "transform TextDocumentEdit" in {
    val tde1 = tde.toClient
    val tde2 = tde1.toShared

    val stringified =
      "{\"textDocument\":{\"uri\":\"uri\",\"version\":1},\"edits\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}]}"

    JSON.stringify(tde1) should be(stringified)

    tde should be(tde2)
  }

  // end of edit

  behavior of "DocumentSymbol transformations"
  val s: SymbolKindClientCapabilities = SymbolKindClientCapabilities(Set(SymbolKind.File))

  it should "transform SymbolKindClientCapabilities" in {
    val s1: ClientSymbolKindClientCapabilities = s.toClient
    val s2: SymbolKindClientCapabilities       = s1.toShared

    val stringified = "{\"valueSet\":[1]}"

    JSON.stringify(s1) should be(stringified)

    s should be(s2)
  }

  it should "transform DocumentSymbolClientCapabilities" in {
    val ds: DocumentSymbolClientCapabilities =
      DocumentSymbolClientCapabilities(None, Some(s), Some(true))
    val ds1: ClientDocumentSymbolClientCapabilities = ds.toClient
    val ds2: DocumentSymbolClientCapabilities       = ds1.toShared

    val stringified = "{\"symbolKind\":{\"valueSet\":[1]},\"hierarchicalDocumentSymbolSupport\":true}"

    JSON.stringify(ds1) should be(stringified)
    ds should be(ds2)
  }

  it should "transform DocumentSymbol" in {
    val ds: DocumentSymbol =
      DocumentSymbol(
        "name",
        SymbolKind(1),
        r,
        r,
        Seq(DocumentSymbol("name", SymbolKind(2), r, r)),
        Some("detail"),
        Some(false)
      )
    val ds1: ClientDocumentSymbol = ds.toClient
    val ds2: DocumentSymbol       = ds1.toShared

    val stringified =
      "{\"name\":\"name\",\"kind\":1,\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"selectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"children\":[{\"name\":\"name\",\"kind\":2,\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"selectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"children\":[]}],\"detail\":\"detail\",\"deprecated\":false}"

    JSON.stringify(ds1) should be(stringified)
    ds should be(ds2)
  }

  it should "transform DocumentSymbolParams" in {
    val ds: DocumentSymbolParams =
      DocumentSymbolParams(tdi)
    val ds1: ClientDocumentSymbolParams = ds.toClient
    val ds2: DocumentSymbolParams       = ds1.toShared

    val stringified = "{\"textDocument\":{\"uri\":\"uri\"}}"

    JSON.stringify(ds1) should be(stringified)
    ds should be(ds2)
  }

  it should "transform SymbolInformation" in {
    val ds: SymbolInformation =
      SymbolInformation("name", SymbolKind(1), l, Some("cn"), Some(true))
    val ds1: ClientSymbolInformation = ds.toClient
    val ds2: SymbolInformation       = ds1.toShared

    val stringified =
      "{\"name\":\"name\",\"kind\":1,\"location\":{\"uri\":\"uri\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}},\"containerName\":\"cn\",\"deprecated\":true}"

    JSON.stringify(ds1) should be(stringified)
    ds should be(ds2)
  }

  // end document symbols

  behavior of "Diagnostics transformations"

  it should "transform DiagnosticClientCapabilities" in {
    val dcc: DiagnosticClientCapabilities        = DiagnosticClientCapabilities(Some(true))
    val dcc1: ClientDiagnosticClientCapabilities = dcc.toClient
    val dcc2: DiagnosticClientCapabilities       = dcc1.toShared

    val stringified = "{\"relatedInformation\":true}"

    JSON.stringify(dcc1) should be(stringified)

    dcc should be(dcc2)
  }

  val dri: DiagnosticRelatedInformation = DiagnosticRelatedInformation(l, "message")
  it should "transform DiagnosticRelatedInformation" in {
    val dri1: ClientDiagnosticRelatedInformation = dri.toClient
    val dri2: DiagnosticRelatedInformation       = dri1.toShared

    val stringified =
      "{\"location\":{\"uri\":\"uri\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}},\"message\":\"message\"}"

    JSON.stringify(dri1) should be(stringified)

    dri should be(dri2)
  }

  it should "transform Diagnostic" in {
    val d: Diagnostic        = Diagnostic(r, "message", Some(DiagnosticSeverity(1)), None, None, None, Some(Seq(dri)))
    val d1: ClientDiagnostic = d.toClient
    val d2: Diagnostic       = d1.toShared

    val stringified =
      "{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"message\":\"message\",\"severity\":1,\"relatedInformation\":[{\"location\":{\"uri\":\"uri\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}},\"message\":\"message\"}]}"

    JSON.stringify(d1) should be(stringified)

    d should be(d2)
  }

  it should "transform Diagnostic with related information" in {
    val d: Diagnostic        = Diagnostic(r, "message", Some(DiagnosticSeverity(1)), None, None, None, Some(Seq(dri)))
    val d1: ClientDiagnostic = d.toClient
    val d2: Diagnostic       = d1.toShared
    d.relatedInformation should be(d2.relatedInformation)
  }

  it should "transform Diagnostic with empty related information" in {
    val d: Diagnostic        = Diagnostic(r, "message", Some(DiagnosticSeverity(1)), None, None, None, None)
    val d1: ClientDiagnostic = d.toClient
    val d2: Diagnostic       = d1.toShared

    d2.relatedInformation should be(None)
  }

  // end of diagnostics

  behavior of "Completion transformations"

  it should "transform CompletionContext" in {
    val cc: CompletionContext        = CompletionContext(CompletionTriggerKind(1), Some('/'))
    val cc1: ClientCompletionContext = cc.toClient
    val cc2: CompletionContext       = cc1.toShared

    val stringified = "{\"triggerKind\":1,\"triggerCharacter\":\"/\"}"

    JSON.stringify(cc1) should be(stringified)

    cc should be(cc2)
  }

  it should "transform CompletionItem" in {
    val cc: CompletionItem = CompletionItem(
      "label",
      Some(CompletionItemKind(1)),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      Some(InsertTextFormat(1)),
      Some(Left(te)),
      Some(Seq(te)),
      Some(Seq('/')),
      None
    )
    val cc1: ClientCompletionItem = cc.toClient
    // completion item should not be converted to shared as it is a server to client dto
//    val cc2: CompletionItem       = cc1.toShared

    val stringified =
      "{\"label\":\"label\",\"kind\":1,\"insertTextFormat\":1,\"textEdit\":{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"},\"additionalTextEdits\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}],\"commitCharacters\":[\"/\"]}"

    JSON.stringify(cc1) should be(stringified)

//    cc should be(cc2)
  }

  behavior of "DocumentLink transformations"

  it should "transform DocumentLink" in {
    val dl: DocumentLink        = DocumentLink(r, "target", None)
    val dl1: ClientDocumentLink = dl.toClient
    val dl2: DocumentLink       = dl1.toShared

    val stringified =
      "{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"target\":\"target\"}"

    JSON.stringify(dl1) should be(stringified)

    dl should be(dl2)
  }

  it should "transform DocumentLinkClientCapabilities" in {
    val dl: DocumentLinkClientCapabilities        = DocumentLinkClientCapabilities(Some(true), Some(false))
    val dl1: ClientDocumentLinkClientCapabilities = dl.toClient
    val dl2: DocumentLinkClientCapabilities       = dl1.toShared

    val stringified = "{\"dynamicRegistration\":true,\"tooltipSupport\":false}"

    JSON.stringify(dl1) should be(stringified)

    dl should be(dl2)
  }

  it should "transform DocumentLinkOptions" in {
    val dl: DocumentLinkOptions        = DocumentLinkOptions(Some(true))
    val dl1: ClientDocumentLinkOptions = dl.toClient
    val dl2: DocumentLinkOptions       = dl1.toShared

    val stringified = "{\"resolveProvider\":true}"

    JSON.stringify(dl1) should be(stringified)

    dl should be(dl2)
  }

  it should "transform DocumentLinkParams" in {
    val dl: DocumentLinkParams        = DocumentLinkParams(tdi)
    val dl1: ClientDocumentLinkParams = dl.toClient
    val dl2: DocumentLinkParams       = dl1.toShared

    val stringified = "{\"textDocument\":{\"uri\":\"uri\"}}"

    JSON.stringify(dl1) should be(stringified)

    dl should be(dl2)
  }

  // end of documentLink

  behavior of "Reference transformations"

  it should "transform ReferenceClientCapabilities" in {
    val r: ReferenceClientCapabilities        = ReferenceClientCapabilities(Some(true))
    val r1: ClientReferenceClientCapabilities = r.toClient
    val r2: ReferenceClientCapabilities       = r1.toShared

    val stringified = "{\"dynamicRegistration\":true}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  val rc: ReferenceContext = ReferenceContext(true)

  it should "transform ReferenceContext" in {
    val rc1: ClientReferenceContext = rc.toClient
    val rc2: ReferenceContext       = rc1.toShared

    val stringified = "{\"includeDeclaration\":true}"

    JSON.stringify(rc1) should be(stringified)

    rc should be(rc2)
  }

  it should "transform ReferenceParams" in {
    val r: ReferenceParams        = ReferenceParams(tdi, p, rc)
    val r1: ClientReferenceParams = r.toClient
    val r2: ReferenceParams       = r1.toShared

    val stringified =
      "{\"textDocument\":{\"uri\":\"uri\"},\"position\":{\"line\":10,\"character\":10},\"context\":{\"includeDeclaration\":true}}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  // end of reference

  behavior of "Rename transformations"

  it should "transform RenameClientCapabilities" in {
    val r: RenameClientCapabilities        = RenameClientCapabilities(Some(true), Some(false))
    val r1: ClientRenameClientCapabilities = r.toClient
    val r2: RenameClientCapabilities       = r1.toShared

    val stringified = "{\"dynamicRegistration\":true,\"prepareSupport\":false}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  it should "transform RenameOptions" in {
    val rc: RenameOptions        = RenameOptions(Some(true))
    val rc1: ClientRenameOptions = rc.toClient
    val rc2: RenameOptions       = rc1.toShared

    val stringified = "{\"prepareProvider\":true}"

    JSON.stringify(rc1) should be(stringified)

    rc should be(rc2)
  }

  it should "transform RenameParams" in {
    val r: RenameParams        = RenameParams(tdi, p, "name")
    val r1: ClientRenameParams = r.toClient
    val r2: RenameParams       = r1.toShared

    val stringified =
      "{\"textDocument\":{\"uri\":\"uri\"},\"position\":{\"line\":10,\"character\":10},\"newName\":\"name\"}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  // end of reference

  behavior of "Telemetry transformations"

  it should "transform TelemetryClientCapabilities" in {
    val r: TelemetryClientCapabilities        = TelemetryClientCapabilities(Some(true))
    val r1: ClientTelemetryClientCapabilities = r.toClient
    val r2: TelemetryClientCapabilities       = r1.toShared

    val stringified = "{\"relatedInformation\":true}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  it should "transform TelemetryMessage" in {
    val r: TelemetryMessage        = TelemetryMessage("event", "type", "message", "uri", 1L, "uuid")
    val r1: ClientTelemetryMessage = r.toClient
    val r2: TelemetryMessage       = r1.toShared

    val stringified =
      "{\"event\":\"event\",\"messageType\":\"type\",\"message\":\"message\",\"uri\":\"uri\",\"time\":\"1\",\"uuid\":\"uuid\"}"

    JSON.stringify(r1) should be(stringified)

    r should be(r2)
  }

  // end of telemetry

  behavior of "Text Sync transformations"
  val tdcce: TextDocumentContentChangeEvent = TextDocumentContentChangeEvent("text", Some(r), Some(1))

  it should "transform DidChangeConfigurationNotificationParams" in {
    val ts: DidChangeConfigurationNotificationParams =
      DidChangeConfigurationNotificationParams(
        Some("uri"),
        "ws",
        Set(Left("dep1"), Right(DependencyConfiguration("dep2", DEPENDENCY)))
          ++ Set(
            Right(DependencyConfiguration("p1", CUSTOM_VALIDATION)),
            Right(DependencyConfiguration("p2", CUSTOM_VALIDATION))
          )
          ++ Set(
            Right(DependencyConfiguration("se1", SEMANTIC_EXTENSION)),
            Right(DependencyConfiguration("se2", SEMANTIC_EXTENSION))
          )
      )
    val ts1: ClientDidChangeConfigurationNotificationParams = ts.toClient
    val ts2: DidChangeConfigurationNotificationParams       = ts1.toShared

    val stringified =
      """{"mainPath":"uri","folder":"ws","dependencies":[{"file":"p1","scope":"custom-validation"},{"file":"dep2","scope":"dependency"},"dep1",{"file":"p2","scope":"custom-validation"},{"file":"se2","scope":"semantic-extension"},{"file":"se1","scope":"semantic-extension"}]}"""
    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform DidChangeTextDocumentParams" in {
    val ts: DidChangeTextDocumentParams        = DidChangeTextDocumentParams(vtdi, Seq(tdcce))
    val ts1: ClientDidChangeTextDocumentParams = ts.toClient
    val ts2: DidChangeTextDocumentParams       = ts1.toShared

    val stringified =
      "{\"textDocument\":{\"uri\":\"uri\",\"version\":1},\"contentChanges\":[{\"text\":\"text\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"rangeLength\":1}]}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform DidCloseTextDocumentParams" in {
    val ts: DidCloseTextDocumentParams        = DidCloseTextDocumentParams(tdi)
    val ts1: ClientDidCloseTextDocumentParams = ts.toClient
    val ts2: DidCloseTextDocumentParams       = ts1.toShared

    val stringified = "{\"textDocument\":{\"uri\":\"uri\"}}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform DidOpenTextDocumentParams" in {
    val ts: DidOpenTextDocumentParams        = DidOpenTextDocumentParams(tdItem)
    val ts1: ClientDidOpenTextDocumentParams = ts.toClient
    val ts2: DidOpenTextDocumentParams       = ts1.toShared

    val stringified =
      "{\"textDocument\":{\"uri\":\"uri\",\"languageId\":\"test\",\"version\":1,\"text\":\"test text\"}}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  val so: SaveOptions = SaveOptions(Some(true))
  it should "transform SaveOptions" in {
    val ts1: ClientSaveOptions = so.toClient
    val ts2: SaveOptions       = ts1.toShared

    val stringified = "{\"includeText\":true}"

    JSON.stringify(ts1) should be(stringified)

    so should be(ts2)
  }

  it should "transform SynchronizationClientCapabilities" in {
    val ts: SynchronizationClientCapabilities =
      SynchronizationClientCapabilities(Some(true), Some(true), Some(true), Some(true))
    val ts1: ClientSynchronizationClientCapabilities = ts.toClient
    val ts2: SynchronizationClientCapabilities       = ts1.toShared

    val stringified = "{\"dynamicRegistration\":true,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform TextDocumentContentChangeEvent" in {
    val tdcce1: ClientTextDocumentContentChangeEvent = tdcce.toClient
    val tdcce2: TextDocumentContentChangeEvent       = tdcce1.toShared

    val stringified =
      "{\"text\":\"text\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"rangeLength\":1}"

    JSON.stringify(tdcce1) should be(stringified)

    tdcce should be(tdcce2)
  }

  it should "transform TextDocumentSyncOptions" in {
    val ts: TextDocumentSyncOptions =
      TextDocumentSyncOptions(Some(true), Some(TextDocumentSyncKind(1)), Some(true), Some(true), Some(so))
    val ts1: ClientTextDocumentSyncOptions = ts.toClient
    val ts2: TextDocumentSyncOptions       = ts1.toShared

    val stringified =
      "{\"openClose\":true,\"change\":1,\"willSave\":true,\"willSaveWaitUntil\":true,\"save\":{\"includeText\":true}}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "Transform TextDocumentClientCapabilities with DocumentFormattingClientCapabilities" in {
    val documentFormattingCapabilities      = DocumentFormattingClientCapabilities(Some(true))
    val documentRangeFormattingCapabilities = DocumentRangeFormattingClientCapabilities(Some(true))
    val textDocumentClientCapabilities =
      TextDocumentClientCapabilities(
        formatting = Some(documentFormattingCapabilities),
        rangeFormatting = Some(documentRangeFormattingCapabilities)
      )
    val ts: ClientTextDocumentClientCapabilities = textDocumentClientCapabilities.toClient
    val sharedConversion                         = ts.toShared
    textDocumentClientCapabilities should be(sharedConversion)
    textDocumentClientCapabilities should be(sharedConversion)
  }

  it should "Transform TextDocumentClientCapabilities without DocumentFormattingClientCapabilities" in {
    val textDocumentClientCapabilities           = TextDocumentClientCapabilities()
    val ts: ClientTextDocumentClientCapabilities = textDocumentClientCapabilities.toClient
    val sharedConversion                         = ts.toShared
    textDocumentClientCapabilities should be(sharedConversion)
    textDocumentClientCapabilities should be(sharedConversion)
  }

  // end of textsync

  behavior of "Workspace transformations"
  val wfce: WorkspaceFoldersChangeEvent = WorkspaceFoldersChangeEvent(List(wf), List(wf))
  val fe: FileEvent                     = FileEvent("uri", FileChangeType(1))

  it should "transform DidChangeConfigurationParams" in {
    val w: DidChangeConfigurationParams =
      DidChangeConfigurationParams(fe.toClient) // todo: check how this should work (similar to ClientCommand?)
    val w1: ClientDidChangeConfigurationParams = w.toClient
    val w2                                     = w1.toShared

    val stringified = "{\"settings\":{\"uri\":\"uri\",\"type\":1}}"

    JSON.stringify(w1) should be(stringified)

    w should be(w2)
  }

  it should "transform DidChangeWatchedFilesParams" in {
    val w: DidChangeWatchedFilesParams        = DidChangeWatchedFilesParams(List(fe))
    val w1: ClientDidChangeWatchedFilesParams = w.toClient
    val w2: DidChangeWatchedFilesParams       = w1.toShared

    val stringified = "{\"changes\":[{\"uri\":\"uri\",\"type\":1}]}"

    JSON.stringify(w1) should be(stringified)

    w should be(w2)
  }

  it should "transform DidChangeWorkspaceFoldersParams" in {
    val w: DidChangeWorkspaceFoldersParams        = DidChangeWorkspaceFoldersParams(wfce)
    val w1: ClientDidChangeWorkspaceFoldersParams = w.toClient
    val w2: DidChangeWorkspaceFoldersParams       = w1.toShared

    val stringified =
      "{\"event\":{\"added\":[{\"uri\":\"uri\",\"name\":\"name\"}],\"deleted\":[{\"uri\":\"uri\",\"name\":\"name\"}]}}"

    JSON.stringify(w1) should be(stringified)

    w should be(w2)
  }

  it should "transform ExecuteCommandParams" in {
    val w: ExecuteCommandParams        = ExecuteCommandParams("command", List("arguments"))
    val w1: ClientExecuteCommandParams = w.toClient
    val w2: ExecuteCommandParams       = w1.toShared

    val stringified = "{\"command\":\"command\",\"arguments\":[\"arguments\"]}"

    JSON.stringify(w1) should be(stringified)

//    w should be(w2) // todo: specific per param?
  }

  it should "transform FileEvent" in {
    val w1: ClientFileEvent = fe.toClient
    val w2: FileEvent       = w1.toShared

    val stringified = "{\"uri\":\"uri\",\"type\":1}"

    JSON.stringify(w1) should be(stringified)

    fe should be(w2)
  }

  it should "transform WorkspaceFoldersChangeEvent" in {
    val w1: ClientWorkspaceFoldersChangeEvent = wfce.toClient
    val w2: WorkspaceFoldersChangeEvent       = w1.toShared

    val stringified =
      "{\"added\":[{\"uri\":\"uri\",\"name\":\"name\"}],\"deleted\":[{\"uri\":\"uri\",\"name\":\"name\"}]}"

    JSON.stringify(w1) should be(stringified)

    wfce should be(w2)
  }

  it should "transform WorkspaceSymbolParams" in {
    val w: WorkspaceSymbolParams        = WorkspaceSymbolParams("query")
    val w1: ClientWorkspaceSymbolParams = w.toClient
    val w2: WorkspaceSymbolParams       = w1.toShared

    val stringified = "{\"query\":\"query\"}"

    JSON.stringify(w1) should be(stringified)

    w should be(w2)
  }

  // end of workspace
  // custom messages
  // todo: migrate custom message test to corresponding modules
//
//  it should "transform ClientCapabilities" in {
//    val cc = AlsClientCapabilities(
//      Some(WorkspaceClientCapabilities()),
//      Some(TextDocumentClientCapabilities()),
//      None,
//      Some(SerializationClientCapabilities(true)),
//      Some(CleanDiagnosticTreeClientCapabilities(true))
//    )
//
//    val cc1 = cc.toClient
//    val cc2 = cc1.toShared
//
//    val stringified =
//      "{\"workspace\":{},\"textDocument\":{},\"serialization\":{\"acceptsNotification\":true},\"cleanDiagnosticTree\":{\"enableCleanDiagnostic\":true}}" // todo: test with textDocument parameters when ready
//
//    JSON.stringify(cc1) should be(stringified)
//
//    cc should be(cc2)
//  }
//
//
//
//  it should "transform IndexDialectParams" in {
//    val ts: IndexDialectParams        = IndexDialectParams("uri", Some("content"))
//    val ts1: ClientIndexDialectParams = ts.toClient
//    val ts2: IndexDialectParams       = ts1.toShared
//
//    val stringified = "{\"uri\":\"uri\",\"content\":\"content\"}"
//
//    JSON.stringify(ts1) should be(stringified)
//
//    ts should be(ts2)
//  }
//
//
//
//  it should "transform DidFocusParams" in {
//    val ts: DidFocusParams        = DidFocusParams("uri", 1)
//    val ts1: ClientDidFocusParams = ts.toClient
//    val ts2: DidFocusParams       = ts1.toShared
//
//    val stringified = "{\"uri\":\"uri\",\"version\":1}"
//
//    JSON.stringify(ts1) should be(stringified)
//
//    ts should be(ts2)
//  }
//
//
//
//  it should "transform WorkspaceEdit" in {
//    val we  = WorkspaceEdit(Map("uri" -> Seq(te)), Seq(Left(tde)))
//    val we1 = we.toClient
//    val we2 = we1.toShared
//
//    val stringified =
//      "{\"changes\":{\"uri\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}]},\"documentChanges\":[{\"textDocument\":{\"uri\":\"uri\",\"version\":1},\"edits\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}]}]}"
//
//    JSON.stringify(we1) should be(stringified)
//
//    we should be(we2)
//  }
//
//  it should "transform InitializeParams" in {
//    val ip: AlsInitializeParams        = AlsInitializeParams(None, None, Some("uri"), None, Some(Seq(wf)), None, None)
//    val ip1: ClientAlsInitializeParams = ip.toClient
//    val ip2: AlsInitializeParams       = ip1.toShared
//
//    val stringified =
//      "{\"processId\":null,\"capabilities\":{},\"trace\":\"off\",\"rootUri\":\"uri\",\"workspaceFolders\":[{\"uri\":\"uri\",\"name\":\"name\"}]}"
//
//    JSON.stringify(ip1) should be(stringified)
//
//    ip.capabilities should be(ip2.capabilities)
//    ip.initializationOptions should be(ip2.initializationOptions)
//    ip.rootUri should be(ip2.rootUri)
//    ip.trace should be(ip2.trace)
//    ip.workspaceFolders should be(ip2.workspaceFolders)
//  }
//
//  it should "transform AlsClientCapabilities" in {
//    val acp: AlsClientCapabilities = AlsClientCapabilities(None,
//      None,
//      None,
//      Some(SerializationClientCapabilities(true)),
//      Some(CleanDiagnosticTreeClientCapabilities(true)))
//    val acp1: ClientAlsClientCapabilities = acp.toClient
//    val acp2: AlsClientCapabilities       = acp1.toShared
//
//    acp.serialization should be(acp2.serialization)
//    acp.cleanDiagnosticTree should be(acp2.cleanDiagnosticTree)
//    acp.serialization.get.acceptsNotification should be(acp2.serialization.get.acceptsNotification)
//    acp.cleanDiagnosticTree.get.enableCleanDiagnostic should be(acp2.cleanDiagnosticTree.get.enableCleanDiagnostic)
//  }
//
//  val sc: AlsServerCapabilities = AlsServerCapabilities(Some(Left(TextDocumentSyncKind(1))), Some(CompletionOptions()))
//  it should "transform ServerCapabilities" in {
//    val sc1: ClientAlsServerCapabilities = sc.toClient
//    val sc2: AlsServerCapabilities       = sc1.toShared
//
//    val stringified =
//      "{\"textDocumentSync\":1,\"completionProvider\":{},\"definitionProvider\":false,\"referencesProvider\":false,\"documentSymbolProvider\":false}"
//
//    JSON.stringify(sc1) should be(stringified)
//
//    sc should be(sc2)
//  }
//
//  it should "transform InitializeResult" in {
//    val ir: AlsInitializeResult        = AlsInitializeResult(sc)
//    val ir1: ClientAlsInitializeResult = ir.toClient
//    val ir2: AlsInitializeResult       = ir1.toShared
//
//    val stringified =
//      "{\"capabilities\":{\"textDocumentSync\":1,\"completionProvider\":{},\"definitionProvider\":false,\"referencesProvider\":false,\"documentSymbolProvider\":false}}"
//
//    JSON.stringify(ir1) should be(stringified)
//
//    ir should be(ir2)
//  }
}
