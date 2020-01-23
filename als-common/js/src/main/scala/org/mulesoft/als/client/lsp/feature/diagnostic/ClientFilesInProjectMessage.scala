package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.workspace.FilesInProjectParams
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

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
