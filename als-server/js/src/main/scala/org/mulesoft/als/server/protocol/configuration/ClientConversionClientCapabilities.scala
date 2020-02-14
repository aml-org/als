package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.serialization.ConversionClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientConversionClientCapabilities extends js.Object {
  def supported: Boolean = js.native
}

object ClientConversionClientCapabilities {
  def apply(internal: ConversionClientCapabilities): ClientConversionClientCapabilities = {
    js.Dynamic
      .literal(
        supported = internal.supported
      )
      .asInstanceOf[ClientConversionClientCapabilities]
  }
}
// $COVERAGE-ON$
