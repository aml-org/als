package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.converter.SEither3
import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.Range

case object PrepareRenameRequestType
    extends RequestType[PrepareRenameParams, Option[SEither3[Range, PrepareRenameResult, PrepareRenameDefaultBehavior]]]

case class PrepareRenameResult(range: Range, placeholder: String)
case class PrepareRenameDefaultBehavior(range: Range, placeholder: String, defaultBehavior: Boolean)
