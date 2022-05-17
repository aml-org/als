package org.mulesoft.lsp.feature.rename

/** Capabilities specific to the `textDocument/rename`
  *
  * @param dynamicRegistration
  *   Whether definition supports dynamic registration.
  * @param prepareSupport
  *   The client supports testing for validity of rename operations before execution.
  */

case class RenameClientCapabilities(dynamicRegistration: Option[Boolean], prepareSupport: Option[Boolean])
