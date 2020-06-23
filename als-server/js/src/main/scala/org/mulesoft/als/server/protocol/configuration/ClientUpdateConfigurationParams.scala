package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientUpdateConfigurationParams extends js.Object {
  def clientAlsFormattingOptions: js.UndefOr[js.Dictionary[ClientAlsFormattingOptions]] = js.native // Nullable
}

object ClientUpdateConfigurationParams {
  def apply(internal: UpdateConfigurationParams): ClientUpdateConfigurationParams = {
    js.Dynamic
      .literal(
        conversion = internal.updateFormatOptionsParams.foreach(f => f.map(v => v._1.toString -> v._2.toClient))
      )
      .asInstanceOf[ClientUpdateConfigurationParams]
  }
}

// $COVERAGE-ON$
