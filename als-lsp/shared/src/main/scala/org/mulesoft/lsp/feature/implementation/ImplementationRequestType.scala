package org.mulesoft.lsp.feature.implementation

import org.mulesoft.lsp.feature.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.RequestType

case object ImplementationRequestType
    extends RequestType[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]]
