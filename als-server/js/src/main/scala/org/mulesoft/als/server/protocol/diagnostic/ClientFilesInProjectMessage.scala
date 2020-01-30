package org.mulesoft.als.server.protocol.diagnostic

import org.mulesoft.lsp.feature.workspace.FilesInProjectParams

import scala.scalajs.js.JSConverters._
import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFilesInProjectMessage extends js.Object {

  def uris: js.Array[String] = js.native
}

object ClientFilesInProjectMessage {
  def apply(internal: FilesInProjectParams): ClientFilesInProjectMessage = {
    js.Dynamic
      .literal(
        uris = internal.uris.toJSArray
      )
      .asInstanceOf[ClientFilesInProjectMessage]
  }
}
// $COVERAGE-ON$