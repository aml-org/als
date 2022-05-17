package org.mulesoft.als.server

import org.mulesoft.als.configuration.{AlsConfiguration, TemplateTypes}
import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeClientCapabilities
import org.mulesoft.als.server.feature.serialization.{SerializationClientCapabilities, SerializedDocument}
import org.mulesoft.als.server.protocol.configuration._
import org.mulesoft.als.server.protocol.convert.LspConvertersClientToShared._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.serialization.ClientSerializedDocument
import org.mulesoft.als.server.protocol.textsync.{
  ClientDidFocusParams,
  ClientIndexDialectParams,
  DidFocusParams,
  IndexDialectParams
}
import org.mulesoft.lsp.configuration.{
  TextDocumentClientCapabilities,
  TraceKind,
  WorkspaceClientCapabilities,
  WorkspaceFolder
}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.scalatest.{FlatSpec, Matchers}

import scala.scalajs.js.JSON

class ClientConversionTest extends FlatSpec with Matchers {

  behavior of "Server Classes"
  private val p: Position                           = Position(10, 10)
  private val r: Range                              = Range(p, p)
  private val te: TextEdit                          = TextEdit(r, "text")
  private val vtdi: VersionedTextDocumentIdentifier = VersionedTextDocumentIdentifier("uri", Some(1))
  private val wf                                    = WorkspaceFolder(Some("uri"), Some("name"))

  it should "transform SerializedDocument" in {
    val s: SerializedDocument        = SerializedDocument("uri", "model")
    val s1: ClientSerializedDocument = s.toClient
    val s2: SerializedDocument       = s1.toShared

    val stringified = "{\"uri\":\"uri\",\"model\":\"model\"}"

    JSON.stringify(s1) should be(stringified)

    s should be(s2)
  }

  it should "transform ClientCapabilities" in {
    val cc = AlsClientCapabilities(
      Some(WorkspaceClientCapabilities()),
      Some(TextDocumentClientCapabilities()),
      None,
      Some(SerializationClientCapabilities(true)),
      Some(CleanDiagnosticTreeClientCapabilities(true))
    )

    val cc1 = cc.toClient
    val cc2 = cc1.toShared

    val stringified =
      "{\"workspace\":{},\"textDocument\":{},\"serialization\":{\"acceptsNotification\":true},\"cleanDiagnosticTree\":{\"enableCleanDiagnostic\":true}}" // todo: test with textDocument parameters when ready

    JSON.stringify(cc1) should be(stringified)

    cc should be(cc2)
  }

  it should "transform IndexDialectParams" in {
    val ts: IndexDialectParams        = IndexDialectParams("uri", Some("content"))
    val ts1: ClientIndexDialectParams = ts.toClient
    val ts2: IndexDialectParams       = ts1.toShared

    val stringified = "{\"uri\":\"uri\",\"content\":\"content\"}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform DidFocusParams" in {
    val ts: DidFocusParams        = DidFocusParams("uri", 1)
    val ts1: ClientDidFocusParams = ts.toClient
    val ts2: DidFocusParams       = ts1.toShared

    val stringified = "{\"uri\":\"uri\",\"version\":1}"

    JSON.stringify(ts1) should be(stringified)

    ts should be(ts2)
  }

  it should "transform InitializeParams" in {
    val ip: AlsInitializeParams = AlsInitializeParams(
      capabilities = None,
      trace = Some(TraceKind.Off),
      rootUri = Some("uri"),
      workspaceFolders = Some(Seq(wf))
    )
    val ip1: ClientAlsInitializeParams = ip.toClient
    val ip2: AlsInitializeParams       = ip1.toShared

    val stringified =
      "{\"processId\":null,\"locale\":null,\"capabilities\":{},\"trace\":\"off\",\"rootUri\":\"uri\",\"workspaceFolders\":[{\"uri\":\"uri\",\"name\":\"name\"}]}"

    JSON.stringify(ip1) should be(stringified)

    ip.capabilities should be(ip2.capabilities)
    ip.initializationOptions should be(ip2.initializationOptions)
    ip.rootUri should be(ip2.rootUri)
    ip.trace should be(ip2.trace)
    ip.workspaceFolders should be(ip2.workspaceFolders)
  }

  it should "transform AlsClientCapabilities" in {
    val acp: AlsClientCapabilities = AlsClientCapabilities(
      None,
      None,
      None,
      Some(SerializationClientCapabilities(true)),
      Some(CleanDiagnosticTreeClientCapabilities(true))
    )
    val acp1: ClientAlsClientCapabilities = acp.toClient
    val acp2: AlsClientCapabilities       = acp1.toShared

    acp.serialization should be(acp2.serialization)
    acp.cleanDiagnosticTree should be(acp2.cleanDiagnosticTree)
    acp.serialization.get.acceptsNotification should be(acp2.serialization.get.acceptsNotification)
    acp.cleanDiagnosticTree.get.enableCleanDiagnostic should be(acp2.cleanDiagnosticTree.get.enableCleanDiagnostic)
  }

  it should "transform ALSConfiguration with templateTypes" in {
    val simpleTemplate = AlsConfiguration(templateType = TemplateTypes.SIMPLE)
    val fullTemplate   = AlsConfiguration(templateType = TemplateTypes.FULL)
    val bothTemplate   = AlsConfiguration(templateType = TemplateTypes.BOTH)
    val noneTemplate   = AlsConfiguration(templateType = TemplateTypes.NONE)
    val woTemplate     = AlsConfiguration()

    val t1: ClientAlsConfiguration = simpleTemplate.toClient
    val t2: ClientAlsConfiguration = fullTemplate.toClient
    val t3: ClientAlsConfiguration = bothTemplate.toClient
    val t4: ClientAlsConfiguration = noneTemplate.toClient
    val t5: ClientAlsConfiguration = woTemplate.toClient

    val s1 = t1.toShared
    val s2 = t2.toShared
    val s3 = t3.toShared
    val s4 = t4.toShared
    val s5 = t5.toShared

    simpleTemplate should be(s1)
    fullTemplate should be(s2)
    bothTemplate should be(s3)
    noneTemplate should be(s4)
    woTemplate should be(s5)
  }
}
