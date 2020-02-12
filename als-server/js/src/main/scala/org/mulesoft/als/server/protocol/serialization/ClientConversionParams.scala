package org.mulesoft.als.server.protocol.serialization

import org.mulesoft.als.server.feature.serialization.ConversionParams

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientConversionParams extends js.Object {

  def uri: String             = js.native
  def target: String          = js.native
  def syntax: UndefOr[String] = js.native
}

object ClientConversionParams {
  def apply(internal: ConversionParams): ClientConversionParams = {
    js.Dynamic
      .literal(
        uri = internal.uri,
        target = internal.target,
        syntax = internal.syntax.orUndefined
      )
      .asInstanceOf[ClientConversionParams]
  }
}
// $COVERAGE-ON$
