package org.mulesoft.als.suggestions.test

import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.{AmfConfigurationPatcher, MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.AccessBundle
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.lsp.feature.completion.CompletionItem
import upickle.default.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets with MarkerFinderTest with AccessBundle {

  protected val dr = new PlatformDirectoryResolver(platform)

  def writeDataToString(data: List[CompletionItem]): String =
    write[List[CompletionItemNode]](data.map(CompletionItemNode.sharedToTransport), 2)

  def suggest(url: String, label: String, alsConfiguration: ALSConfigurationState): Future[Seq[CompletionItem]] = {

    for {
      content <- alsConfiguration.fetchContent(url)
      r       <- suggestFromFile(content.stream.toString, url, label, alsConfiguration)
    } yield {
      r
    }
  }

  def suggestFromFile(
      content: String,
      url: String,
      label: String,
      configurationState: ALSConfigurationState
  ): Future[Seq[CompletionItem]] = {
    var position        = 0
    val fileContentsStr = content
    val markerInfo      = this.findMarker(fileContentsStr, label)

    position = markerInfo.offset
    val resourceLoader = AmfConfigurationPatcher.resourceLoaderForFile(url, markerInfo.content)
    val newAlsConfig   = createNewStateWithLoaders(configurationState, resourceLoader)
    new Suggestions(AlsConfiguration(), dr, accessBundle(newAlsConfig))
      .initialized()
      .suggest(url, position, snippetsSupport = true, None)
  }

  protected def createNewStateWithLoaders(
      configurationState: ALSConfigurationState,
      resourceLoader: ResourceLoader
  ): ALSConfigurationState =
    ALSConfigurationState(
      configurationState.editorState,
      configurationState.projectState,
      Some(resourceLoader),
      configurationState.newCachingLogic
    )
}
