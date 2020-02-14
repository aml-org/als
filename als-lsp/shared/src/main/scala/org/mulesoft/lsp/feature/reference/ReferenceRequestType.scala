package org.mulesoft.lsp.feature.reference

import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.RequestType

case object ReferenceRequestType extends RequestType[ReferenceParams, Seq[Location]]
