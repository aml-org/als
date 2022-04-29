package org.mulesoft.lsp.feature.rename

/** Rename options
  *
  * @param prepareProvider
  *   Renames should be checked and tested before being executed.
  */
case class RenameOptions(prepareProvider: Option[Boolean] = None)
