package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RamlHeaderCompletionProvider extends BasicPrefixExtractor {
  def build(uri: String, header: String, position: Position): BasicCompletionProvider =
    new BasicCompletionProvider(
      getPrefix(header, position).dropWhile(_ == ' '),
      position,
      () => Future(Seq(RawSuggestion("#%RAML 1.0", isAKey = false)))
    )
}
