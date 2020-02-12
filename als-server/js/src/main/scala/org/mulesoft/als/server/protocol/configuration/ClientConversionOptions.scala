package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.serialization.ConversionRequestOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientConversionOptions extends js.Object {
  def supported: js.Array[ClientConversionConfig] = js.native
}

object ClientConversionOptions {
  def apply(internal: ConversionRequestOptions): ClientConversionOptions = {
    js.Dynamic
      .literal(supported = internal.supported.map(c => ClientConversionConfig(c)).toJSArray)
      .asInstanceOf[ClientConversionOptions]
  }
}

// $COVERAGE-OFF$
