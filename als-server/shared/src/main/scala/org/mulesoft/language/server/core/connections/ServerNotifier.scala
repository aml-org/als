package org.mulesoft.language.server.core.connections

import common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, ILocation, OpenedDocument}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol

import scala.concurrent.Future

trait ServerNotifier {

  def notifyDocumentOpened(openedDocument: OpenedDocument): Unit

  def notifyDocumentChanged(changedDocument: ChangedDocument): Unit

  def notifyDocumentClosed(path: String): Unit

  def notifyDocumentCompletion(uri: String, offset: Position): Future[Seq[Suggestion]]

  def notifyDocumentStructure(uri: String): Future[Seq[DocumentSymbol]]

  def notifyFindReferences(uri: String, offset: Position): Future[Seq[ILocation]]

  def notifyOpenDeclaration(uri: String, offset: Position): Future[Seq[ILocation]]

  def notifyRename(uri: String, offset: Position, newName: String): Future[Seq[ChangedDocument]]
}
