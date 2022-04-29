package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.feature.hover.MarkupKind.MarkupKind

/** The hover request is sent from the client to the server to request hover information at a given text document
  * position.
  *
  * @param dynamicRegistration
  *   : Whether hover supports dynamic registration
  * @param contentFormat
  *   : Client supports the follow content formats for the content property. The order describes the preferred format of
  *   the client.
  */
case class HoverClientCapabilities(dynamicRegistration: Option[Boolean], contentFormat: Seq[MarkupKind])
