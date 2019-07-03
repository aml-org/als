package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.lsp.configuration.{
  ClientCapabilities => InternalCapabilities,
  InitializeParams => InternalParams,
  TextDocumentClientCapabilities => InternalDocumentClient
}
import org.scalatest.FunSuite

import scala.collection.JavaConverters._

class LsConvertionsTest extends FunSuite {

  test("Empty InitializeParams Conversions") {
    val internalParams: InternalParams = new InitializeParams()
    succeed
  }

  test("Empty sons of InitializeParams Conversions") {
    val clientParams = new InitializeParams()
    clientParams.setCapabilities(new ClientCapabilities())
    val folder = new WorkspaceFolder()
    clientParams.setWorkspaceFolders(List(folder).asJava)
    clientParams.setInitializationOptions(new Object())
    val internalParams: InternalParams = clientParams
    succeed
  }

  test("Empty sons of client capabilities") {
    val capabilities = new ClientCapabilities()
    capabilities.setTextDocument(new TextDocumentClientCapabilities())
    capabilities.setWorkspace(new WorkspaceClientCapabilities())
    val internal: InternalCapabilities = capabilities
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

}
