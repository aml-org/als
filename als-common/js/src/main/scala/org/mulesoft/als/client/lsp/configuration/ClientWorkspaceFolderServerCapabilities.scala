package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient.eitherToUnion
import org.mulesoft.lsp.configuration.WorkspaceFolderServerCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|

@js.native
trait ClientWorkspaceFolderServerCapabilities extends js.Object {
  val supported: js.UndefOr[Boolean]
  val changeNotifications: js.UndefOr[String | Boolean]
}


object ClientWorkspaceFolderServerCapabilities {

  def apply(internal: WorkspaceFolderServerCapabilities): ClientWorkspaceFolderServerCapabilities =
    js.Dynamic
      .literal(
        supported = internal.supported.orUndefined,
        changeNotifications = internal.changeNotifications.map[String | Boolean](eitherToUnion).orUndefined.asInstanceOf[js.Any]
      )
      .asInstanceOf[ClientWorkspaceFolderServerCapabilities]
}
