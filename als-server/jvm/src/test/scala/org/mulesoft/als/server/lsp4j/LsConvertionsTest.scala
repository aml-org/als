package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.als.server.lsp4j.extension.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.protocol.configuration
import org.mulesoft.lsp.LspConversions._
import org.mulesoft.lsp.configuration.{TextDocumentClientCapabilities => InternalDocumentClient}
import org.scalatest.FunSuite
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic => InternalDiagnostic}

import java.util
import scala.collection.JavaConverters._
class LsConvertionsTest extends FunSuite {

  test("Empty InitializeParams Conversions") {
    val internalParams: configuration.AlsInitializeParams = new AlsInitializeParams()
    succeed
  }

  test("Empty sons of InitializeParams Conversions") {
    val clientParams = new AlsInitializeParams()
    clientParams.setCapabilities(new ClientCapabilities())
    val folder = new WorkspaceFolder()
    clientParams.setWorkspaceFolders(List(folder).asJava)
    clientParams.setInitializationOptions(new Object())
    val internalParams: configuration.AlsInitializeParams = clientParams
    succeed
  }

  test("Empty sons of client capabilities") {
    val capabilities = new AlsClientCapabilities()
    capabilities.setTextDocument(new TextDocumentClientCapabilities())
    capabilities.setWorkspace(new WorkspaceClientCapabilities())
    val internal: configuration.AlsClientCapabilities = capabilities
  }

  test("Empty sons of text document client") {
    val documentClient = new TextDocumentClientCapabilities()
    documentClient.setSynchronization(new SynchronizationCapabilities())
    documentClient.setPublishDiagnostics(new PublishDiagnosticsCapabilities())
    documentClient.setCompletion(new CompletionCapabilities())
    documentClient.setReferences(new ReferencesCapabilities())
    documentClient.setDocumentSymbol(new DocumentSymbolCapabilities())
    documentClient.setDefinition(new DefinitionCapabilities())
    documentClient.setRename(new RenameCapabilities())
    val internal: InternalDocumentClient = documentClient
    succeed
  }

  test("Full Diagnostic conversion") {
    val diagnostic = new Diagnostic()
    diagnostic.setCode(2)
    diagnostic.setMessage("Message")
    diagnostic.setTags( new util.ArrayList[DiagnosticTag])
    val start = new Position(0,1)
    val end = new Position(1,1)
    diagnostic.setRange(new Range(start, end))
    diagnostic.setRelatedInformation(new util.ArrayList[DiagnosticRelatedInformation])
    diagnostic.setSeverity(DiagnosticSeverity.Information)
    diagnostic.setSource("Source")
    val internalDiagnostic: InternalDiagnostic = diagnostic
    succeed
  }

  test("Diagnostic Only mandatory fields conversion") {
    val start = new Position(0,1);
    val end = new Position(1,1);
    val diagnostic = new Diagnostic(new Range(start, end), "Message")
    val internalDiagnostic: InternalDiagnostic = diagnostic
    succeed
  }

  test("Empty diagnostic conversion") {
    val diagnostic = new Diagnostic()
    try{
      val internalDiagnostic: InternalDiagnostic = diagnostic
    }
    catch {
      case _: NullPointerException => succeed
      case e => fail("Unexpected error on empty diagnostic conversion test", e)
    }
  }

}
