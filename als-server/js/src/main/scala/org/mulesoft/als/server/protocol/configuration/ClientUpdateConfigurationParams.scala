package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientUpdateConfigurationParams extends js.Object {
  def clientUpdateFormatOptionsParams: UndefOr[ClientUpdateFormatOptionsParams] = js.native // Nullable
}

object ClientUpdateConfigurationParams {
  def apply(internal: UpdateConfigurationParams): ClientUpdateConfigurationParams = {
    js.Dynamic
      .literal(
        conversion = internal.updateFormatOptionsParams.map(_.toClient).orNull
      )
      .asInstanceOf[ClientUpdateConfigurationParams]
  }
}

// $COVERAGE-ON$
