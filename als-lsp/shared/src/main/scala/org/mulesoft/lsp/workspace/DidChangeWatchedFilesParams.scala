package org.mulesoft.lsp.workspace

case class DidChangeWatchedFilesParams(changes: List[FileEvent])
