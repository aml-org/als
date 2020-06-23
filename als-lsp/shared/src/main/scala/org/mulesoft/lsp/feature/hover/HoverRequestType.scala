package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.feature.RequestType

case object HoverRequestType extends RequestType[HoverParams, Hover]
