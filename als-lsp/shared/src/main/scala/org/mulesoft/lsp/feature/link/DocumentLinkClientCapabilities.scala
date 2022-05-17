package org.mulesoft.lsp.feature.link

/** Capabilities specific to the `textDocument/documentLink`.
  *
  * @param dynamicRegistration
  *   Whether documentLink supports dynamic registration.
  * @param tooltipSupport
  *   Whether the client support the `tooltip` property on `DocumentLink`.
  */
case class DocumentLinkClientCapabilities(dynamicRegistration: Option[Boolean], tooltipSupport: Option[Boolean])
