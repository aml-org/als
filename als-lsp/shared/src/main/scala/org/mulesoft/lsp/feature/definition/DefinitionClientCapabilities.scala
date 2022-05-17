package org.mulesoft.lsp.feature.definition

/** Capabilities specific to the `textDocument/definition`.
  *
  * @param dynamicRegistration
  *   Whether definition supports dynamic registration.
  * @param linkSupport
  *   The client supports additional metadata in the form of definition links.
  */

case class DefinitionClientCapabilities(dynamicRegistration: Option[Boolean], linkSupport: Option[Boolean])
