package org.mulesoft.lsp.feature.folding

object FoldingRangeKind extends Enumeration {
  type FoldingRangeKind = String
  val Comment = "comment"
  val Imports = "imports"
  val Region  = "region"
}
