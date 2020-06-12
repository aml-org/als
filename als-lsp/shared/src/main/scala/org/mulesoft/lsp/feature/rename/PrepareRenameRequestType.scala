package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.Range

case object PrepareRenameRequestType
    extends RequestType[PrepareRenameParams, Option[Either[Range, PrepareRenameResult]]]

case class PrepareRenameResult(range: Range, placeholder: String)
