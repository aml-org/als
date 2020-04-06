package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.feature.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.RequestType

case object TypeDefinitionRequestType
    extends RequestType[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]]
