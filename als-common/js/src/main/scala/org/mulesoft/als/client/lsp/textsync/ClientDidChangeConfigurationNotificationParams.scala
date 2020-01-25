package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeConfigurationNotificationParams extends js.Object {
  def mainUri: String                = js.native
  def dependencies: js.Array[String] = js.native
}

object ClientDidChangeConfigurationNotificationParams {
  def apply(internal: DidChangeConfigurationNotificationParams): ClientDidChangeConfigurationNotificationParams =
    js.Dynamic
      .literal(
        mainUri = internal.mainUri,
        dependencies = internal.dependencies.toJSArray
      )
      .asInstanceOf[ClientDidChangeConfigurationNotificationParams]
}

// $COVERAGE-ON$