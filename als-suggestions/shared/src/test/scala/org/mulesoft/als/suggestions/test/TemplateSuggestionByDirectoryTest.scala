package org.mulesoft.als.suggestions.test

import org.mulesoft.als.common.AmfConfigurationPatcher
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.configuration.TemplateTypes.TemplateTypes
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.common.io.SyncFile
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.Future

trait TemplateSuggestionByDirectoryTest extends SuggestionByDirectoryTest {

  def templateType: TemplateTypes

  override def suggestFromFile(content: String,
                               url: String,
                               label: String,
                               configurationState: ALSConfigurationState): Future[Seq[CompletionItem]] = {
    var position        = 0
    val fileContentsStr = content
    val markerInfo      = this.findMarker(fileContentsStr, label)

    position = markerInfo.offset
    val resourceLoader = AmfConfigurationPatcher.resourceLoaderForFile(url, markerInfo.content)
    val newAlsConfig =
      ALSConfigurationState(configurationState.editorState, configurationState.projectState, Some(resourceLoader))
    new Suggestions(AlsConfiguration(templateType = templateType), dr, accessBundle(newAlsConfig))
      .initialized()
      .suggest(url, position, snippetsSupport = true, None)
  }

  override def expectedFile(f: SyncFile): String = {
    s"${f.parent}${platform.fs.separatorChar}expected${platform.fs.separatorChar}${f.name}-$templateType.json"
  }
}
