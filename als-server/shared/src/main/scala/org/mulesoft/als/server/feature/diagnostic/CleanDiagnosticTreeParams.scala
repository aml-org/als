package org.mulesoft.als.server.feature.diagnostic

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

/** The clean validation provides the list of all the files depending of the given uri (tree) and the eventual list of
  * diagnostics for each one.
  *
  * @param textDocument
  */
case class CleanDiagnosticTreeParams(textDocument: TextDocumentIdentifier)
