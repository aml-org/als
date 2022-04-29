package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.RequestType

case object DocumentSymbolRequestType
    extends RequestType[DocumentSymbolParams, Either[Seq[SymbolInformation], Seq[DocumentSymbol]]]
