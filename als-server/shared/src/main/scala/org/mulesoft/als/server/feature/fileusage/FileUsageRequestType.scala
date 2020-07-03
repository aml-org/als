package org.mulesoft.als.server.feature.fileusage

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier}

case object FileUsageRequestType extends RequestType[TextDocumentIdentifier, Seq[Location]]
