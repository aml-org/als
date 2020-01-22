package org.mulesoft.lsp.configuration

/**
  * @param capabilities The capabilities the language server provides.
  */
case class AlsInitializeResult(capabilities: AlsServerCapabilities)

object AlsInitializeResult {

  def empty: AlsInitializeResult = AlsInitializeResult(AlsServerCapabilities.empty)
}
