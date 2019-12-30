package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.StaticRegistrationOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

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
