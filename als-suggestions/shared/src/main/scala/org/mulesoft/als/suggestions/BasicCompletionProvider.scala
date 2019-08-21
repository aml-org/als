package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Suggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BasicCompletionProvider(prefix: String, position: Position, suggestions: () => Future[Seq[RawSuggestion]])
    extends CompletionProvider {
  override def suggest(): Future[Seq[Suggestion]] =
    suggestions()
      .map(
        _.distinct
          .filter(_.newText startsWith prefix)
          .map(rs => {
            SuggestionStyler.asSuggestionImpl(s => s.text)(rs.toSuggestion(prefix), position)
          }))
}

trait BasicPrefixExtractor {
  def getPrefix(content: String, position: Position): String = {
    val lines = content.linesIterator.drop(position.line)
    if (lines hasNext) {
      if (position.column > 0)
        lines.next().substring(0, position.column)
      else lines.next()
    } else ""
  }
}
