package org.mulesoft.lsp.feature.folding

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientFoldingRange extends js.Object {
  def startLine: Int                  = js.native
  def startCharacter: js.UndefOr[Int] = js.native
  def endLine: Int                    = js.native
  def endCharacter: js.UndefOr[Int]   = js.native
  def kind: js.UndefOr[String]        = js.native
}

object ClientFoldingRange {
  def apply(internal: FoldingRange): ClientFoldingRange =
    js.Dynamic
      .literal(
        startLine = internal.startLine,
        startCharacter = internal.startCharacter.orUndefined,
        endLine = internal.endLine,
        endCharacter = internal.endCharacter.orUndefined,
        kind = internal.kind.orUndefined
      )
      .asInstanceOf[ClientFoldingRange]
}

// $COVERAGE-ON$
