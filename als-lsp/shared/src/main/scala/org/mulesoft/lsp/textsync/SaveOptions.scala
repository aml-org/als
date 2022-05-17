package org.mulesoft.lsp.textsync

/** Save options.
  *
  * @param includeText
  *   The client is supposed to include the content on save.
  */
case class SaveOptions(includeText: Option[Boolean])
