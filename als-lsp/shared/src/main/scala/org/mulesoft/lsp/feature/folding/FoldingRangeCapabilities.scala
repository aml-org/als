package org.mulesoft.lsp.feature.folding

case class FoldingRangeCapabilities(
    dynamicRegistration: Option[Boolean],
    rangeLimit: Option[Int],
    lineFoldingOnly: Option[Boolean]
)
