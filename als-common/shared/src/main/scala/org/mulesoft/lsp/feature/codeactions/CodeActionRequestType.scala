package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.RequestType

case object CodeActionRequestType extends RequestType[CodeActionParams, Seq[CodeAction]]
