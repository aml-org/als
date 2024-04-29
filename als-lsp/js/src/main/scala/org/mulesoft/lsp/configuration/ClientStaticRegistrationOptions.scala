package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientStaticRegistrationOptions extends js.Object {
  def id: js.UndefOr[String] = js.native
}

object ClientStaticRegistrationOptions {
  def apply(internal: StaticRegistrationOptions): ClientStaticRegistrationOptions =
    js.Dynamic
      .literal(id = internal.id.orUndefined)
      .asInstanceOf[ClientStaticRegistrationOptions]
}

// $COVERAGE-ON$
