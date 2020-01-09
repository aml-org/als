package org.mulesoft.als.client.lsp.workspace

import org.mulesoft.lsp.workspace.FileEvent

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileEvent extends js.Object {
  def uri: String = js.native
  def `type`: Int = js.native
}

object ClientFileEvent {
  def apply(internal: FileEvent): ClientFileEvent =
    js.Dynamic
      .literal(uri = internal.uri, `type` = internal.`type`.id)
      .asInstanceOf[ClientFileEvent]
}
// $COVERAGE-ON$
