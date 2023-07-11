package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientFileOperationPatternOptionsConverter

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileOperationPattern extends js.Object {
  val glob: String                = js.native
  val matches: js.UndefOr[String] = js.native
  val options: js.UndefOr[ClientFileOperationPatternOptions]
}
object ClientFileOperationPattern {
  def apply(internal: FileOperationPattern): ClientFileOperationPattern =
    js.Dynamic
      .literal(
        glob = internal.glob,
        matches = internal.matches.orUndefined,
        options = internal.options.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientFileOperationPattern]
}

// $COVERAGE-ON$
