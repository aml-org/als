package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier

/** The document change notification is sent from the client to the server to signal changes to a text document.
  *
  * @param textDocument
  *   The document that did change. The version number points to the version after all provided content changes have
  *   been applied.
  * @param contentChanges
  *   The actual content changes. The content changes describe single state changes to the document. So if there are two
  *   content changes c1 and c2 for a document in state S then c1 move the document to S and c2 to S&#39;&#39;.
  */

case class DidChangeTextDocumentParams(
    textDocument: VersionedTextDocumentIdentifier,
    contentChanges: Seq[TextDocumentContentChangeEvent]
)
