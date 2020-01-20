package org.mulesoft.lsp.client

trait LanguageClient[S] extends LspLanguageClient with AlsLanguageClient[S] {}
