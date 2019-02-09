package org.mulesoft.language.server.core.connections

import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, OpenedDocument, Position}

import scala.concurrent.Future

trait ServerNotifier {

  def notifyDocumentOpened(openedDocument: OpenedDocument): Unit

  def notifyDocumentChanged(changedDocument: ChangedDocument): Unit

  def notifyDocumentClosed(path: String): Unit

  def notifyDocumentCompletion(uri: String, offset: Position): Future[Seq[Suggestion]]
}
