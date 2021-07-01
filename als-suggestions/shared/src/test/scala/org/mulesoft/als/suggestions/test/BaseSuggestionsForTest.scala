package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.common.{MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.completion.CompletionItem
import upickle.default.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets with MarkerFinderTest {

  protected val dr = new PlatformDirectoryResolver(platform)

  def writeDataToString(data: List[CompletionItem]): String =
    write[List[CompletionItemNode]](data.map(CompletionItemNode.sharedToTransport), 2)

  def suggest(url: String, label: String, dialectContent: Option[String]): Future[Seq[CompletionItem]] = {

    for {
      content <- platform.resolve(url)
      r       <- suggestFromFile(content.stream.toString, url, content.mime, label, dialectContent)
    } yield {
      r
    }
  }

  def suggestFromFile(content: String,
                      url: String,
                      mime: Option[String],
                      label: String,
                      dialect: Option[String]): Future[Seq[CompletionItem]] = {

    var position        = 0
    val fileContentsStr = content
    val markerInfo      = this.findMarker(fileContentsStr, label)

    position = markerInfo.offset

    val environment = this.buildEnvironment(url, markerInfo.content, mime)
    val instance    = AmfInstance(platform, environment)
    for {
      _ <- instance.init()
      s <- {
        dialect.map(d => instance.alsAmlPlugin.registry.registerDialect(d)).getOrElse(Future.unit).map { _ =>
          new Suggestions(platform, environment, AlsConfiguration(), dr, instance).initialized()
        }
      }
      suggestions <- s.suggest(url, position, snippetsSupport = true, None)
    } yield suggestions
  }

  def buildEnvironment(fileUrl: String, content: String, mime: Option[String]): Environment = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] =
        Future.successful(new Content(content, fileUrl))
    })

    loaders ++= platform.loaders()

    Environment(loaders)
  }
}
