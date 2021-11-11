package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.AmfParseContext

object HeaderCompletionProviderBuilder extends BasicPrefixExtractor {
  def build(uri: String,
            header: String,
            position: Position,
            parseContext: AmfParseContext,
            configuration: AlsConfigurationReader): BasicCompletionProvider =
    new BasicCompletionProvider(
      getPrefix(header, position).dropWhile(_ == ' '),
      position,
      () =>
        CompletionPluginsRegistryHeaders.pluginSuggestions(
          HeaderCompletionParams(uri, header, position, parseContext, configuration))
    )
}
