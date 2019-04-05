package org.mulesoft.als.server

import org.mulesoft.lsp.feature.RequestHandler

trait RequestModule[C, S] extends InitializableModule[C, S] {
  def getRequestHandlers: Seq[RequestHandler[_, _]]
}
