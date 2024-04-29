package org.mulesoft.als.server.protocol.textsync

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientIndexDialectParams extends js.Object {
  def uri: String              = js.native
  def content: UndefOr[String] = js.native
}

object ClientIndexDialectParams {
  def apply(internal: IndexDialectParams): ClientIndexDialectParams =
    js.Dynamic
      .literal(
        uri = internal.uri,
        content = internal.content.orUndefined
      )
      .asInstanceOf[ClientIndexDialectParams]
}

// $COVERAGE-ON$
