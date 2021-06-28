package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.lsp.configuration.ClientFormattingOptions
import scala.scalajs.js.JSConverters._
import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsConfiguration extends js.Object {
  def formattingOptions: js.Dictionary[ClientFormattingOptions] = js.native
  def templateType: String                                      = js.native

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
        templateType = internal.getTemplateType
      )
      .asInstanceOf[ClientAlsConfiguration]
  }
}

// $COVERAGE-ON$
