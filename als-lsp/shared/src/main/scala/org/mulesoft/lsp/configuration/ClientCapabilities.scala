package org.mulesoft.lsp.configuration

/** ClientCapabilities now define capabilities for dynamic registration, workspace and text document features the client
  * supports. The experimental can be used to pass experimental capabilities under development. For future compatibility
  * a ClientCapabilities object literal can have more properties set than currently defined. Servers receiving a
  * ClientCapabilities object literal with unknown properties should ignore these properties. A missing property should
  * be interpreted as an absence of the capability. If a missing property normally defines sub properties, all missing
  * sub properties should be interpreted as an absence of the corresponding capability.
  *
  * @param workspace
  *   Workspace specific client capabilities.
  * @param textDocument
  *   Text document specific client capabilities.
  * @param experimental
  *   Experimental client capabilities.
  */
class ClientCapabilities(
    val workspace: Option[WorkspaceClientCapabilities] = None,
    val textDocument: Option[TextDocumentClientCapabilities] = None,
    val experimental: Option[AnyRef] = None
)
