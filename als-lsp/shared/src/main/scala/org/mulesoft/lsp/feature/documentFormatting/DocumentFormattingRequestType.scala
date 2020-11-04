package org.mulesoft.lsp.feature.documentFormatting

import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.RequestType

case object DocumentFormattingRequestType extends RequestType[DocumentFormattingParams, Seq[TextEdit]]
