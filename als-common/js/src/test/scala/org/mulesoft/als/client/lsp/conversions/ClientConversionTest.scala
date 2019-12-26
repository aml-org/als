package org.mulesoft.als.client.lsp.conversions

import org.mulesoft.als.client.convert.LspConvertersClientToShared._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.common.{
  ClientLocationLink,
  ClientPosition,
  ClientRange,
  ClientTextDocumentIdentifier,
  ClientTextDocumentItem,
  ClientTextDocumentPositionParams,
  ClientVersionedTextDocumentIdentifier
}
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbolClientCapabilities,
  ClientSymbolKindClientCapabilities
}
import org.mulesoft.lsp.command.Command
import org.mulesoft.lsp.common.{
  LocationLink,
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  TextDocumentPositionParams,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbolClientCapabilities,
  SymbolKind,
  SymbolKindClientCapabilities
}
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

    JSON.stringify(r1) should be(rStringified) // check with json parser?

    r should be(r2)
  }

  it should "transform LocationLink" in {
    val llStringified =
      "{\"targetUri\":\"uri\",\"targetRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"targetSelectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}},\"originSelectionRange\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":10}}}"
    val ll: LocationLink        = LocationLink("uri", r, r, Some(r))
    val ll1: ClientLocationLink = ll.toClient
    val ll2: LocationLink       = ll1.toShared

    JSON.stringify(ll1) should be(llStringified) // check with json parser?

    ll should be(ll2)
  }

  val tdi: TextDocumentIdentifier = TextDocumentIdentifier("uri")

  it should "transform TextDocumentIdentifier" in {
    val tdiStringified                     = "{\"uri\":\"uri\"}"
    val tdi1: ClientTextDocumentIdentifier = tdi.toClient
    val tdi2: TextDocumentIdentifier       = tdi1.toShared

    JSON.stringify(tdi1) should be(tdiStringified) // check with json parser?

    tdi should be(tdi2)
  }

  it should "transform VersionedTextDocumentIdentifier" in {
    val vtdiStringified                              = "{\"uri\":\"uri\",\"version\":1}"
    val vtdi: VersionedTextDocumentIdentifier        = VersionedTextDocumentIdentifier("uri", Some(1))
    val vtdi1: ClientVersionedTextDocumentIdentifier = vtdi.toClient
    val vtdi2: VersionedTextDocumentIdentifier       = vtdi1.toShared

    JSON.stringify(vtdi1) should be(vtdiStringified) // check with json parser?

    vtdi should be(vtdi2)
  }

  it should "transform TextDocumentItem" in {
    val tdiStringified               = "{\"uri\":\"uri\",\"languageId\":\"test\",\"version\":1,\"text\":\"test text\"}"
    val tdi: TextDocumentItem        = TextDocumentItem("uri", "test", 1, "test text")
    val tdi1: ClientTextDocumentItem = tdi.toClient
    val tdi2: TextDocumentItem       = tdi1.toShared

    JSON.stringify(tdi1) should be(tdiStringified) // check with json parser?
    tdi should be(tdi2)
  }

  it should "transform TextDocumentPositionParams" in {
    val tdiStringified                          = "{\"textDocument\":{\"uri\":\"uri\"},\"position\":{\"line\":10,\"character\":10}}"
    val tdpp: TextDocumentPositionParams        = TextDocumentPositionParams(tdi, p)
    val tdpp1: ClientTextDocumentPositionParams = tdpp.toClient
    val tdpp2: TextDocumentPositionParams       = tdpp1.toShared

    JSON.stringify(tdpp1) should be(tdiStringified) // check with json parser?
    tdpp.position should be(tdpp2.position)
    tdpp.textDocument should be(tdpp2.textDocument)
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

    JSON.stringify(c1) should be(stringified) // check with json parser?

    c should be(c2)
  }

  // end command

  behavior of "DocumentSymbol transformations"
  val s: SymbolKindClientCapabilities = SymbolKindClientCapabilities(Set(SymbolKind.File))

  it should "transform SymbolKindClientCapabilities" in {
    val s1: ClientSymbolKindClientCapabilities = s.toClient
    val s2: SymbolKindClientCapabilities       = s1.toShared

    val stringified = "{\"valueSet\":[1]}"

    JSON.stringify(s1) should be(stringified) // check with json parser?

    s should be(s2)
  }

  it should "transform DocumentSymbolClientCapabilities" in {
    val ds: DocumentSymbolClientCapabilities =
      DocumentSymbolClientCapabilities(None, Some(s), Some(true))
    val ds1: ClientDocumentSymbolClientCapabilities = ds.toClient
    val ds2: DocumentSymbolClientCapabilities       = ds1.toShared

    val stringified = "{\"symbolKind\":{\"valueSet\":[1]},\"hierarchicalDocumentSymbolSupport\":true}"

    JSON.stringify(ds1) should be(stringified) // check with json parser?
    ds should be(ds2)
  }

  // end document symbol
}
