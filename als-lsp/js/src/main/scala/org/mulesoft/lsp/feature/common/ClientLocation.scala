package org.mulesoft.lsp.feature.common

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientLocation extends js.Object {
  def uri: String        = js.native
  def range: ClientRange = js.native
}

object ClientLocation {
  def apply(internal: Location): ClientLocation =
    js.Dynamic
      .literal(
        uri = internal.uri,
        range = internal.range.toClient
      )
      .asInstanceOf[ClientLocation]
}

// $COVERAGE-ON$
