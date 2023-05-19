package org.mulesoft.als.server.protocol.filecontents

import org.mulesoft.als.server.feature.fileusage.filecontents.FileContentsResponse

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenMap

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileContentsResponse extends js.Object {
  def fs: js.Dictionary[String] = js.native
}

object ClientFileContentsResponse {
  def apply[S](internal: FileContentsResponse): ClientFileContentsResponse =
    js.Dynamic
      .literal(
        fs = internal.fs.toJSDictionary
      )
      .asInstanceOf[ClientFileContentsResponse]
}

// $COVERAGE-ON$
