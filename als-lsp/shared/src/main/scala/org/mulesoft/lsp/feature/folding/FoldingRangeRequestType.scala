package org.mulesoft.lsp.feature.folding

import org.mulesoft.lsp.feature.RequestType

case object FoldingRangeRequestType extends RequestType[FoldingRangeParams, Seq[FoldingRange]]
