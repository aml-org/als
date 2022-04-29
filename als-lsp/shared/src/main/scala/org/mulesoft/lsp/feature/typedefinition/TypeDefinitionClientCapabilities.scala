package org.mulesoft.lsp.feature.typedefinition

/** Capabilities specific to the `textDocument/implementation`.
  *
  * @param dynamicRegistration
  *   Whether definition supports dynamic registration.
  * @param linkSupport
  *   The client supports additional metadata in the form of definition links.
  */
case class TypeDefinitionClientCapabilities(dynamicRegistration: Option[Boolean], linkSupport: Option[Boolean])
