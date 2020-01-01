package org.mulesoft.als.client.lsp.feature.link

import org.mulesoft.als.client.lsp.common.ClientRange
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import js.JSConverters._

@js.native
trait ClientDocumentLink extends js.Object {
  def range: ClientRange       = js.native
  def target: String           = js.native
  def data: UndefOr[js.Object] = js.native
}

object ClientDocumentLink {
  def apply(internal: DocumentLink): ClientDocumentLink =
    js.Dynamic
      .literal(range = internal.range.toClient, target = internal.target, data = internal.data.collect {
        case js: js.Object => js
      }.orUndefined)
      .asInstanceOf[ClientDocumentLink]
}
