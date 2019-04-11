package org.mulesoft.lsp.configuration

/**
  * @param capabilities The capabilities the language server provides.
  */
case class InitializeResult(capabilities: ServerCapabilities)