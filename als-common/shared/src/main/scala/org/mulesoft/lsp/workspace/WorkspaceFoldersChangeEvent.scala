package org.mulesoft.lsp.workspace

import org.mulesoft.lsp.configuration.WorkspaceFolder

case class WorkspaceFoldersChangeEvent(added: List[WorkspaceFolder], deleted: List[WorkspaceFolder])
