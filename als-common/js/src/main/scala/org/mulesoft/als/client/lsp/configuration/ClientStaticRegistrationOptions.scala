package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.StaticRegistrationOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "StaticRegistrationOptions")
class ClientStaticRegistrationOptions(private val internal: StaticRegistrationOptions) {
  def id: js.UndefOr[String] = internal.id.orUndefined
}
