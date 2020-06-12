package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.{Location, LocationLink}

case object TypeDefinitionRequestType
    extends RequestType[TypeDefinitionParams, Either[Seq[Location], Seq[LocationLink]]]
