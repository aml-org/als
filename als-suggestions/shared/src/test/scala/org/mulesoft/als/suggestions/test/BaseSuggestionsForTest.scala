package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfmanager.{CustomDialects, DialectInitializer, InitOptions}
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.mulesoft.lsp.server.{AmfConfiguration, LanguageServerEnvironmentInstance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets {

  protected val dr = new PlatformDirectoryResolver(platform)

  def suggest(url: String, customDialect: Option[CustomDialects]): Future[Seq[CompletionItem]] = {

    for {
      content <- platform.resolve(url)
      r       <- suggestFromFile(content.stream.toString, url, content.mime, customDialect)
    } yield {
      customDialect.foreach(c => {
        AMLPlugin.registry.remove(c.url)
        DialectInitializer.removeInitialized(c.name)
      })
      r
    }
  }

  def suggestFromFile(content: String,
                      url: String,
                      mime: Option[String],
                      customDialect: Option[CustomDialects]): Future[Seq[CompletionItem]] = {

    var position = 0
    for {
      s <- Future.successful {
        val fileContentsStr = content
        val markerInfo      = this.findMarker(fileContentsStr)

        position = markerInfo.position

        val environment = this.buildEnvironment(url, markerInfo.patchedContent.original, mime)
        new Suggestions(AmfConfiguration(LanguageServerEnvironmentInstance(platform, environment, dr)))
      }
      _           <- s.init(InitOptions.AllProfiles.withCustomDialects(customDialect.toSeq))
      suggestions <- s.suggest(url, position, snippetsSupport = true)
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
