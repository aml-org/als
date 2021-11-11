package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientDependencyConfigurationConverter

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{UndefOr, |}
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeConfigurationNotificationParams extends js.Object {
  def mainUri: UndefOr[String]                                       = js.native
  def folder: String                                                 = js.native
  def dependencies: js.Array[String | ClientDependencyConfiguration] = js.native
}

object ClientDidChangeConfigurationNotificationParams {
  def apply(internal: DidChangeConfigurationNotificationParams): ClientDidChangeConfigurationNotificationParams =
    js.Dynamic
      .literal(
        mainUri = internal.mainUri.orUndefined,
        folder = internal.folder,
        dependencies = internal.dependencies.map {
          case Left(value)  => value
          case Right(value) => value.toClient
        }.toJSArray
      )
      .asInstanceOf[ClientDidChangeConfigurationNotificationParams]
}

// $COVERAGE-ON$
