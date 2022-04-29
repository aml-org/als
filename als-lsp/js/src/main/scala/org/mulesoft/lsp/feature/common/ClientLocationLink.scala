package org.mulesoft.lsp.feature.common

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientLocationLink extends js.Object {
  def targetUri: String                             = js.native
  def targetRange: ClientRange                      = js.native
  def targetSelectionRange: ClientRange             = js.native
  def originSelectionRange: js.UndefOr[ClientRange] = js.native
}

object ClientLocationLink {
  def apply(internal: LocationLink): ClientLocationLink =
    js.Dynamic
      .literal(
        targetUri = internal.targetUri,
        targetRange = internal.targetRange.toClient,
        targetSelectionRange = internal.targetSelectionRange.toClient,
        originSelectionRange = internal.originSelectionRange.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientLocationLink]
}

// $COVERAGE-ON$
