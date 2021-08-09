package org.mulesoft.lsp.textsync

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeConfigurationNotificationParams extends js.Object {
  def mainUri: String                            = js.native
  def dependencies: js.Array[String]             = js.native
  def customValidationProfiles: js.Array[String] = js.native
}

object ClientDidChangeConfigurationNotificationParams {
  def apply(internal: DidChangeConfigurationNotificationParams): ClientDidChangeConfigurationNotificationParams =
    js.Dynamic
      .literal(
        mainUri = internal.mainUri,
        dependencies = internal.dependencies.toJSArray,
        customValidationProfiles = internal.customValidationProfiles.toJSArray
      )
      .asInstanceOf[ClientDidChangeConfigurationNotificationParams]
}

// $COVERAGE-ON$
