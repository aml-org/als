package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Suggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HeaderCompletionProviderBuilder extends BasicPrefixExtractor {
  def build(uri: String, header: String, position: Position): BasicCompletionProvider =
    new BasicCompletionProvider(
      getPrefix(header, position).dropWhile(_ == ' '),
      position,
      () => CompletionPluginsRegistryHeaders.pluginSuggestions(HeaderCompletionParams(uri, header, position))
    )
}
