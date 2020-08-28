package org.mulesoft.lsp.feature.common

trait TextDocumentRegistrationOptions {
  val documentSelector: Option[Seq[DocumentFilter]] = None
}
