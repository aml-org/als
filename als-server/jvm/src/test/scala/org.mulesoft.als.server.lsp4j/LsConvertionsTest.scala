package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.als.server.lsp4j.extension.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.protocol.configuration
import org.mulesoft.lsp.configuration.{TextDocumentClientCapabilities => InternalDocumentClient}
import org.scalatest.FunSuite
import org.mulesoft.lsp.LspConversions._
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

}
