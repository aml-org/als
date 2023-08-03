package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientFileOperationPatternConverter

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileOperationFilter extends js.Object {
  val scheme: js.UndefOr[String]          = js.native
  val pattern: ClientFileOperationPattern = js.native
}

object ClientFileOperationFilter {
  def apply(internal: FileOperationFilter): ClientFileOperationFilter =
    js.Dynamic
      .literal(
        scheme = internal.scheme.orUndefined,
        pattern = internal.pattern.toClient
      )
      .asInstanceOf[ClientFileOperationFilter]
}

// $COVERAGE-ON$
