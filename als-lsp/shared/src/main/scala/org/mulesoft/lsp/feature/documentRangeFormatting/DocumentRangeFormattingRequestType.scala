package org.mulesoft.lsp.feature.documentRangeFormatting

import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.RequestType

case object DocumentRangeFormattingRequestType extends RequestType[DocumentRangeFormattingParams, Seq[TextEdit]]
