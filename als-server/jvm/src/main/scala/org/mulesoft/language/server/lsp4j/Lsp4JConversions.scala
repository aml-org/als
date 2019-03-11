package org.mulesoft.language.server.lsp4j

import java.util
import java.util.concurrent.CompletableFuture

import common.dtoTypes.{Position, PositionRange}
import org.eclipse.lsp4j.{
  CompletionItem,
  SymbolKind,
  TextEdit,
  WorkspaceEdit,
  DocumentSymbol => Lsp4JDocumentSymbol,
  Location => Lsp4JLocation,
  Position => Lsp4JPosition,
  Range => Lsp4JRange
}
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, ILocation}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.compat.java8.FutureConverters._

object Lsp4JConversions {

  private def javaList[F, T](items: Seq[F], convert: F => T): util.List[T] = items.map(convert).toList.asJava

  implicit def javaFuture[F, T](future: Future[F], convert: F => T)(
      implicit context: ExecutionContext): CompletableFuture[T] =
    future.map[T](convert).toJava.toCompletableFuture

  implicit def lsp4JWorkspaceEdit(cDocuments: Seq[ChangedDocument]): WorkspaceEdit = {
    val map: util.Map[String, util.List[TextEdit]] = new util.HashMap[String, util.List[TextEdit]]()
    cDocuments.foreach(elem => {
      val list: util.List[TextEdit] = new util.LinkedList[TextEdit]()
      elem.textEdits.getOrElse(Nil).foreach(te => list.add(new TextEdit(te.range, te.text)))
      map.put(elem.uri, list)
    })
    new WorkspaceEdit(map)
  }

  implicit def lsp4JPosition(position: Position): Lsp4JPosition = new Lsp4JPosition(position.line, position.column)

  implicit def position(position: Lsp4JPosition): Position = new Position(position.getLine, position.getCharacter)

  implicit def lsp4JRange(range: PositionRange): Lsp4JRange =
    new Lsp4JRange(lsp4JPosition(range.start), range.end)

  implicit def lsp4JLocation(location: ILocation): Lsp4JLocation =
    new Lsp4JLocation(s"file://${location.uri}", location.posRange)

  implicit def lsp4JLocations(locations: Seq[ILocation]): util.List[Lsp4JLocation] =
    javaList(locations, lsp4JLocation)

  implicit def completionItem(suggestion: Suggestion): CompletionItem = {
    val result = new CompletionItem(suggestion.displayText)
    result.setInsertText(suggestion.text)
    result.setDetail(suggestion.category)

    result
  }

  implicit def completionItems(suggestions: Seq[Suggestion]): util.List[CompletionItem] =
    javaList(suggestions, completionItem)

  implicit def lsp4JDocumentSymbol(symbol: DocumentSymbol): Lsp4JDocumentSymbol = {
    val result =
      new Lsp4JDocumentSymbol(symbol.name, SymbolKind.forValue(symbol.kind.index), symbol.range, symbol.selectionRange)

    result.setDeprecated(symbol.deprecated)
    result.setChildren(javaList(symbol.children, lsp4JDocumentSymbol))

    result
  }

  implicit def lsp4JDocumentSymbols(symbols: Seq[DocumentSymbol]): util.List[Lsp4JDocumentSymbol] =
    javaList(symbols, lsp4JDocumentSymbol)

}
