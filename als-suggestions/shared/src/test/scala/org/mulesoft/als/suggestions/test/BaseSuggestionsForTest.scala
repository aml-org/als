package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfintegration.{AmfInstance, InitOptions}
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets {

  protected val dr = new PlatformDirectoryResolver(platform)

  def suggest(url: String, dialectContent: Option[String]): Future[Seq[CompletionItem]] = {

    for {
      content <- platform.resolve(url)
      r       <- suggestFromFile(content.stream.toString, url, content.mime, dialectContent)
    } yield {
      r
    }
  }

  def suggestFromFile(content: String,
                      url: String,
                      mime: Option[String],
                      dialect: Option[String]): Future[Seq[CompletionItem]] = {

    var position        = 0
    val fileContentsStr = content
    val markerInfo      = this.findMarker(fileContentsStr)

    position = markerInfo.position

    val environment = this.buildEnvironment(url, markerInfo.patchedContent.original, mime)
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

  def findMarker(str: String,
                 label: String = "*",
                 cut: Boolean = false,
                 labels: Array[String] = Array("*")): MarkerInfo = {
    val position = str.indexOf(label)

    val str1 = {
      if (cut && position >= 0) {
        str.substring(0, position)
      } else
        str
    }

    if (position < 0)
      new MarkerInfo(PatchedContent(str1, str1, Nil), str1.length)
    else {
      val rawContent = str1.replace(label, "")

      val preparedContent = ContentPatcher(rawContent, position, YAML).prepareContent()
      new MarkerInfo(preparedContent, position)
    }

  }
}

class MarkerInfo(val patchedContent: PatchedContent, val position: Int) {}
