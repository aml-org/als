package org.mulesoft.als.server.protocol.client

import org.mulesoft.lsp.client.LspLanguageClient

trait LanguageClient[S] extends LspLanguageClient with AlsLanguageClient[S] {}
