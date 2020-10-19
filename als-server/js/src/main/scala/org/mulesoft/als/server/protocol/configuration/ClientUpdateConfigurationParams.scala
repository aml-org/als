package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams
import org.mulesoft.lsp.configuration.ClientFormattingOptions
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientFormattingOptionsConverter

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientUpdateConfigurationParams extends js.Object {
  def clientAlsFormattingOptions: js.UndefOr[js.Dictionary[ClientFormattingOptions]] = js.native // Nullable
  def clientGenericOptions: js.Dictionary[js.Any]                                    = js.native
}

object ClientUpdateConfigurationParams {
  def apply(internal: UpdateConfigurationParams): ClientUpdateConfigurationParams = {
    js.Dynamic
      .literal(
        conversion = internal.updateFormatOptionsParams.foreach(f => f.map(v => v._1 -> v._2.toClient))
      )
      .asInstanceOf[ClientUpdateConfigurationParams]
  }
}

// $COVERAGE-ON$
