package org.mulesoft.lsp.feature.reference

/** Capabilities specific to the `textDocument/references`
  *
  * @param dynamicRegistration
  *   Whether references supports dynamic registration.
  */

case class ReferenceClientCapabilities(dynamicRegistration: Option[Boolean])
