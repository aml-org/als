package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.{Location, LocationLink}

case object DefinitionRequestType extends RequestType[DefinitionParams, Either[Seq[Location], Seq[LocationLink]]]
