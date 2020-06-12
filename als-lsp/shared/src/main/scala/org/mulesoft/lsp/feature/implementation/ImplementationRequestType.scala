package org.mulesoft.lsp.feature.implementation

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.{Location, LocationLink}

case object ImplementationRequestType
    extends RequestType[ImplementationParams, Either[Seq[Location], Seq[LocationLink]]]
