package org.mulesoft.lsp.feature.implementation

/** Capabilities specific to the `textDocument/implementation`.
  *
  * @param dynamicRegistration
  *   Whether definition supports dynamic registration.
  * @param linkSupport
  *   The client supports additional metadata in the form of definition links.
  */
case class ImplementationClientCapabilities(dynamicRegistration: Option[Boolean], linkSupport: Option[Boolean])
