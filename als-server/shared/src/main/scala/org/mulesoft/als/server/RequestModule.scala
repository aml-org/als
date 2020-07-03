package org.mulesoft.als.server

import org.mulesoft.lsp.InitializableModule
import org.mulesoft.lsp.feature.TelemeteredRequestHandler

trait RequestModule[C, S] extends InitializableModule[C, S] {
  def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]]
}
