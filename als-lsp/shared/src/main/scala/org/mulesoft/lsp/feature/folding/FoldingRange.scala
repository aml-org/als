package org.mulesoft.lsp.feature.folding

import org.mulesoft.lsp.feature.folding.FoldingRangeKind.FoldingRangeKind

case class FoldingRange(
    startLine: Int,
    startCharacter: Option[Int],
    endLine: Int,
    endCharacter: Option[Int],
    kind: Option[FoldingRangeKind]
)
