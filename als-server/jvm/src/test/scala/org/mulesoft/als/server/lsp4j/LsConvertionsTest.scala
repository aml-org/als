package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.als.server.lsp4j.extension.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.protocol.configuration
import org.mulesoft.lsp.LspConversions._
import org.mulesoft.lsp.configuration.{TextDocumentClientCapabilities => InternalDocumentClient}
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic => InternalDiagnostic}
import org.scalatest.funsuite.AnyFunSuite

import java.util
import scala.collection.JavaConverters._
class LsConvertionsTest extends AnyFunSuite {

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
    clientParams.setHotReload(true)
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
    val clientDiagnostic: Diagnostic = new Diagnostic()
    clientDiagnostic.setCode(2)
    clientDiagnostic.setCodeDescription(new DiagnosticCodeDescription("https://a.ml/error-codes/code/1"))
    clientDiagnostic.setMessage("Message")
    clientDiagnostic.setTags(new util.ArrayList[DiagnosticTag])
    val start = new Position(0, 1)
    val end   = new Position(1, 1)
    clientDiagnostic.setRange(new Range(start, end))
    clientDiagnostic.setRelatedInformation(new util.ArrayList[DiagnosticRelatedInformation])
    clientDiagnostic.setSeverity(DiagnosticSeverity.Information)
    clientDiagnostic.setSource("Source")
    val internalDiagnostic: InternalDiagnostic = clientDiagnostic
    succeed
  }

  test("Diagnostic Only mandatory fields conversion") {
    val start                                  = new Position(0, 1);
    val end                                    = new Position(1, 1);
    val clientDiagnostic                       = new Diagnostic(new Range(start, end), "Message")
    val internalDiagnostic: InternalDiagnostic = clientDiagnostic
    succeed
  }

  test("Empty clientDiagnostic conversion") {
    val clientDiagnostic = new Diagnostic()
    try {
      val internalDiagnostic: InternalDiagnostic = clientDiagnostic
    } catch {
      case _: NullPointerException => succeed
      case e: Throwable            => fail("Unexpected error on empty clientDiagnostic conversion test", e)
    }
  }

  test("Diagnostic with empty related information") {
    val start                                  = new Position(0, 1);
    val end                                    = new Position(1, 1);
    val clientDiagnostic                       = new Diagnostic(new Range(start, end), "Message")
    val internalDiagnostic: InternalDiagnostic = clientDiagnostic
    val relatedInformation                     = internalDiagnostic.relatedInformation
    succeed
  }

  test("Diagnostic with related information set") {
    val start              = new Position(0, 1);
    val end                = new Position(1, 1);
    val clientDiagnostic   = new Diagnostic(new Range(start, end), "Message")
    val relatedInformation = new DiagnosticRelatedInformation(new Location("uri", new Range(start, end)), "Message")
    clientDiagnostic.setRelatedInformation(List(relatedInformation).asJava)
    val internalDiagnostic: InternalDiagnostic = clientDiagnostic
    val internalRelatedInformation             = internalDiagnostic.relatedInformation
    succeed
  }

}
