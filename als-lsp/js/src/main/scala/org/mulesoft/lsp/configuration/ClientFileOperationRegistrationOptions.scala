package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientFileOperationFilterConverter

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileOperationRegistrationOptions extends js.Object {
  val filters: js.Array[ClientFileOperationFilter] = js.native
}

object ClientFileOperationRegistrationOptions {
  def apply(internal: FileOperationRegistrationOptions): ClientFileOperationRegistrationOptions =
    js.Dynamic
      .literal(
        filters = internal.filters.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientFileOperationRegistrationOptions]
}

// $COVERAGE-ON$
