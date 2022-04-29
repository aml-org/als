package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

/** The document close notification is sent from the client to the server when the document got closed in the client.
  * The document’s truth now exists where the document’s Uri points to (e.g. if the document’s Uri is a file Uri the
  * truth now exists on disk). As with the open notification the close notification is about managing the document’s
  * content. Receiving a close notification doesn't mean that the document was open in an editor before. A close
  * notification requires a previous open notification to be sent. Note that a server’s ability to fulfill requests is
  * independent of whether a text document is open or closed.
  *
  * @param textDocument
  *   The document that was closed.
  */

case class DidCloseTextDocumentParams(textDocument: TextDocumentIdentifier)
