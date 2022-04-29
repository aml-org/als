package org.mulesoft.lsp.feature.link

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentLink extends js.Object {
  def range: ClientRange       = js.native
  def target: String           = js.native
  def data: UndefOr[js.Object] = js.native
}

object ClientDocumentLink {
  def apply(internal: DocumentLink): ClientDocumentLink =
    js.Dynamic
      .literal(
        range = internal.range.toClient,
        target = internal.target,
        data = internal.data.collect { case js: js.Object =>
          js
        }.orUndefined
      )
      .asInstanceOf[ClientDocumentLink]
}

// $COVERAGE-ON$
