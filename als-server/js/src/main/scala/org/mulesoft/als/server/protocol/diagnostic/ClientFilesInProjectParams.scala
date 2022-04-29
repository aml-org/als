package org.mulesoft.als.server.protocol.diagnostic

import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFilesInProjectParams extends js.Object {

  def uris: js.Array[String] = js.native
}

object ClientFilesInProjectParams {
  def apply(internal: FilesInProjectParams): ClientFilesInProjectParams = {
    js.Dynamic
      .literal(
        uris = internal.uris.toJSArray
      )
      .asInstanceOf[ClientFilesInProjectParams]
  }
}
// $COVERAGE-ON$
