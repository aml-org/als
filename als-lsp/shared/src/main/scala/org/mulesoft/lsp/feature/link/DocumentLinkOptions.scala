package org.mulesoft.lsp.feature.link

/** Document link options
  *
  * @param resolveProvider
  *   Document links have a resolve provider as well.
  */
case class DocumentLinkOptions(resolveProvider: Option[Boolean] = None)
