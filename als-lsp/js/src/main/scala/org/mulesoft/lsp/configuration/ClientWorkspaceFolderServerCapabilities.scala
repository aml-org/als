package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.eitherToUnion
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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
        changeNotifications =
          internal.changeNotifications.map[String | Boolean](eitherToUnion).orUndefined.asInstanceOf[js.Any]
      )
      .asInstanceOf[ClientWorkspaceFolderServerCapabilities]
}

// $COVERAGE-ON$
