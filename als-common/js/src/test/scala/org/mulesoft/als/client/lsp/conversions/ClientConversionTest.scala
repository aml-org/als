package org.mulesoft.als.client.lsp.conversions

import org.mulesoft.als.client.convert.LspConvertersClientToShared._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.common.{
  ClientLocation,
  ClientLocationLink,
  ClientPosition,
  ClientRange,
  ClientTextDocumentIdentifier,
  ClientTextDocumentItem,
  ClientTextDocumentPositionParams,
  ClientVersionedTextDocumentIdentifier
}
import org.mulesoft.als.client.lsp.configuration.{
  ClientInitializeParams,
  ClientInitializeResult,
  ClientServerCapabilities
}
import org.mulesoft.als.client.lsp.feature.completion.{ClientCompletionContext, ClientCompletionItem}
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
  WorkspaceClientCapabilities,
  WorkspaceFolder
}
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.completion.{
  CompletionContext,
  CompletionItem,
  CompletionItemKind,
  CompletionOptions,
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
  DocumentSymbol,
  DocumentSymbolClientCapabilities,
  DocumentSymbolParams,
  SymbolInformation,
  SymbolKind,
  SymbolKindClientCapabilities
}
import org.mulesoft.lsp.textsync.TextDocumentSyncKind
import org.scalatest.{FlatSpec, Matchers}

import scala.scalajs.js.JSON

class ClientConversionTest extends FlatSpec with Matchers {

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

  it should "transform TextDocumentItem" in {
    val tdiStringified               = "{\"uri\":\"uri\",\"languageId\":\"test\",\"version\":1,\"text\":\"test text\"}"
    val tdi: TextDocumentItem        = TextDocumentItem("uri", "test", 1, "test text")
    val tdi1: ClientTextDocumentItem = tdi.toClient
    val tdi2: TextDocumentItem       = tdi1.toShared

    JSON.stringify(tdi1) should be(tdiStringified)
    tdi should be(tdi2)
  }

  it should "transform TextDocumentPositionParams" in {
    val tdiStringified                          = "{\"textDocument\":{\"uri\":\"uri\"},\"position\":{\"line\":10,\"character\":10}}"
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

  it should "transform ClientCapabilities" in {
    val cc  = ClientCapabilities(Some(WorkspaceClientCapabilities()), Some(TextDocumentClientCapabilities()), None)
    val cc1 = cc.toClient
    val cc2 = cc1.toShared

    val stringified = "{\"workspace\":{},\"textDocument\":{}}" // todo: test with textDocument parameters when ready

    JSON.stringify(cc1) should be(stringified)

    cc should be(cc2)
  }

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

  it should "transform InitializeParams" in {
    val ip: InitializeParams        = InitializeParams(None, None, Some("uri"), None, Some(Seq(wf)), None, None)
    val ip1: ClientInitializeParams = ip.toClient
    val ip2: InitializeParams       = ip1.toShared

    val stringified =
      "{\"capabilities\":{},\"trace\":0,\"rootUri\":\"uri\",\"workspaceFolders\":[{\"uri\":\"uri\",\"name\":\"name\"}]}"

    JSON.stringify(ip1) should be(stringified)

    ip.capabilities should be(ip2.capabilities)
    ip.initializationOptions should be(ip2.initializationOptions)
    ip.rootUri should be(ip2.rootUri)
    ip.trace should be(ip2.trace)
    ip.workspaceFolders should be(ip2.workspaceFolders)
  }

  val sc: ServerCapabilities = ServerCapabilities(Some(Left(TextDocumentSyncKind(1))), Some(CompletionOptions()))
  it should "transform ServerCapabilities" in {
    val sc1: ClientServerCapabilities = sc.toClient
    val sc2: ServerCapabilities       = sc1.toShared

    val stringified =
      "{\"textDocumentSync\":1,\"completionProvider\":{},\"definitionProvider\":false,\"referencesProvider\":false,\"documentSymbolProvider\":false}"

    JSON.stringify(sc1) should be(stringified)

    sc should be(sc2)
  }

  it should "transform InitializeResult" in {
    val ir: InitializeResult        = InitializeResult(sc)
    val ir1: ClientInitializeResult = ir.toClient
    val ir2: InitializeResult       = ir1.toShared

    val stringified =
      "{\"capabilities\":{\"textDocumentSync\":1,\"completionProvider\":{},\"definitionProvider\":false,\"referencesProvider\":false,\"documentSymbolProvider\":false}}"

    JSON.stringify(ir1) should be(stringified)

    ir should be(ir2)
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

  it should "transform WorkspaceEdit" in {
    val we  = WorkspaceEdit(Map("uri" -> Seq(te)), Seq(Left(tde)))
    val we1 = we.toClient
    val we2 = we1.toShared

    val stringified =
      "{\"changes\":{\"uri\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}]},\"documentChanges\":[{\"textDocument\":{\"uri\":\"uri\",\"version\":1},\"edits\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}]}]}"

    JSON.stringify(we1) should be(stringified)

    we should be(we2)
  }

  //end of edit

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
      DocumentSymbol("name",
                     SymbolKind(1),
                     r,
                     r,
                     Seq(DocumentSymbol("name", SymbolKind(2), r, r)),
                     Some("detail"),
                     Some(false))
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
    val d: Diagnostic        = Diagnostic(r, "message", Some(DiagnosticSeverity(1)), None, None, Seq(dri))
    val d1: ClientDiagnostic = d.toClient
    val d2: Diagnostic       = d1.toShared

    val stringified =
      "{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"message\":\"message\",\"severity\":1,\"relatedInformation\":[{\"location\":{\"uri\":\"uri\",\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}},\"message\":\"message\"}]}"

    JSON.stringify(d1) should be(stringified)

    d should be(d2)
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
    val cc: CompletionItem = CompletionItem("label",
                                            Some(CompletionItemKind(1)),
                                            None,
                                            None,
                                            None,
                                            None,
                                            None,
                                            None,
                                            None,
                                            Some(InsertTextFormat(1)),
                                            Some(te),
                                            Some(Seq(te)),
                                            Some(Seq('/')),
                                            None)
    val cc1: ClientCompletionItem = cc.toClient
    val cc2: CompletionItem       = cc1.toShared

    val stringified =
      "{\"label\":\"label\",\"kind\":1,\"insertTextFormat\":1,\"textEdit\":{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"},\"additionalTextEdits\":[{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"newText\":\"text\"}],\"commitCharacters\":[\"/\"]}"

    JSON.stringify(cc1) should be(stringified)

    cc should be(cc2)
  }
}
