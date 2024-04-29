package org.mulesoft.lsp.feature.codeactions

/** Capabilities specific to the `textDocument/codeAction`
  *
  * @param dynamicRegistration
  *   Whether code action supports dynamic registration.
  * @param codeActionLiteralSupport
  *   The client supports code action literals as a valid response of the `textDocument/codeAction` request.
  * @param isPreferredSupport
  *   Whether code action supports the `isPreferred` property.
  */
case class CodeActionCapabilities(
    dynamicRegistration: Option[Boolean],
    codeActionLiteralSupport: Option[CodeActionLiteralSupportCapabilities],
    isPreferredSupport: Option[Boolean]
)
