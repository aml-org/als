package org.mulesoft.als.suggestions.test

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.{MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.completion.CompletionItem
import upickle.default.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets with MarkerFinderTest {

  protected val dr = new PlatformDirectoryResolver(platform)

  protected val defaultAmfConfiguration: AmfConfigurationWrapper = AmfConfigurationWrapper()

  def writeDataToString(data: List[CompletionItem]): String =
    write[List[CompletionItemNode]](data.map(CompletionItemNode.sharedToTransport), 2)

  def suggest(url: String,
              label: String,
              dialectContent: Option[String],
              amfConfiguration: AmfConfigurationWrapper = defaultAmfConfiguration): Future[Seq[CompletionItem]] = {

    for {
      content <- amfConfiguration.fetchContent(url)
      r       <- suggestFromFile(content.stream.toString, url, label, dialectContent)
    } yield {
      r
    }
  }

  def suggestFromFile(content: String,
                      url: String,
                      label: String,
                      dialect: Option[String]): Future[Seq[CompletionItem]] = {

    var position        = 0
    val fileContentsStr = content
    val markerInfo      = this.findMarker(fileContentsStr, label)

    position = markerInfo.offset

    val resourceLoader        = AmfConfigurationWrapper.resourceLoaderForFile(url, markerInfo.content)
    val dialectUrl            = "file:///dialect.yaml"
    val dialectResourceLoader = dialect.map(d => AmfConfigurationWrapper.resourceLoaderForFile(dialectUrl, d))
    val amfConfiguration      = AmfConfigurationWrapper(Seq(resourceLoader))
    for {
      s <- {
        dialectResourceLoader
          .map(drl => {
            amfConfiguration.withResourceLoader(drl)
            amfConfiguration
              .parse(dialectUrl)
              .map(r =>
                r.result.baseUnit match {
                  case d: Dialect => amfConfiguration.registerDialect(d)
              })
          })
          .getOrElse(Future.unit)
          .map { _ =>
            new Suggestions(AlsConfiguration(), dr, amfConfiguration).initialized()
          }
      }
      suggestions <- s.suggest(url, position, snippetsSupport = true, None)
    } yield suggestions
  }
}
