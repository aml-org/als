package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceFolder extends js.Object {
  def uri: js.UndefOr[String] = js.native

  def name: js.UndefOr[String] = js.native
}

object ClientWorkspaceFolder {
  def apply(internal: WorkspaceFolder): ClientWorkspaceFolder =
    js.Dynamic
      .literal(uri = internal.uri.orUndefined, name = internal.name.orUndefined)
      .asInstanceOf[ClientWorkspaceFolder]
}

// $COVERAGE-ON$
