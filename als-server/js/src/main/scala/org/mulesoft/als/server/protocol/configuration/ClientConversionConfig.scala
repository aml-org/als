package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.serialization.ConversionConfig
import org.mulesoft.lsp.ConfigType

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientConversionConfig extends js.Object {

  def from: String = js.native
  def to: String   = js.native
}

object ClientConversionConfig {
  def apply(internal: ConversionConfig): ClientConversionConfig = {
    js.Dynamic
      .literal(
        from = internal.from,
        to = internal.to
      )
      .asInstanceOf[ClientConversionConfig]
  }
}
// $COVERAGE-ON$
