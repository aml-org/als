package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Suggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompletionProviderHeaders(uri: String, header: String, position: Position) extends CompletionProvider {

  private def getPrefix(content: String, position: Position): String = {
    val lines = content.linesIterator.drop(position.line)
    if (lines hasNext) {
      if (position.column > 0)
        lines.next().substring(0, position.column)
      else lines.next()
    } else ""
  }

  override def suggest(): Future[Seq[Suggestion]] = {
    val innerPrefix = getPrefix(header, position).dropWhile(_ == ' ')

    CompletionPluginsRegistryHeaders
      .pluginSuggestions(HeaderCompletionParams(uri, header, position))
      .map(_.filter(_.newText startsWith innerPrefix).map(_.toSuggestion(innerPrefix)))
  }
}
