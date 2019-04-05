package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.RequestType

case object RenameRequestType extends RequestType[RenameParams, WorkspaceEdit]
