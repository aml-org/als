package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.lsp.configuration.ClientFormattingOptions

import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.scalajs.js.UndefOr
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsConfiguration extends js.Object {
  def formattingOptions: js.Dictionary[ClientFormattingOptions] = js.native
  def templateType: String                                      = js.native
  def prettyPrintSerialization: UndefOr[Boolean]                = js.native
}

object ClientAlsConfiguration {
  def apply(internal: AlsConfiguration): ClientAlsConfiguration = {
    js.Dynamic
      .literal(
        formattingOptions = internal.getFormatOptions
          .map({
            case (k, v) => (k -> ClientFormattingOptions(v))
          })
          .toJSDictionary,
        templateType = internal.getTemplateType,
        prettyPrintSerialization = internal.getShouldPrettyPrintSerialization
      )
      .asInstanceOf[ClientAlsConfiguration]
  }
}

// $COVERAGE-ON$
