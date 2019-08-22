package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.RequestType

case object DefinitionRequestType extends RequestType[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]]
