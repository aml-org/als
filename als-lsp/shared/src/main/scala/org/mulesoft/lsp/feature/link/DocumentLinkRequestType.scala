package org.mulesoft.lsp.feature.link

import org.mulesoft.lsp.feature.RequestType

case object DocumentLinkRequestType extends RequestType[DocumentLinkParams, Seq[DocumentLink]]
