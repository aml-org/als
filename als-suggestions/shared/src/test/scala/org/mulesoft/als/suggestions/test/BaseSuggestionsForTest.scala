package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.suggestions.client.{Suggestion, Suggestions}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.amfmanager.{CustomDialects, DialectInitializer, InitOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseSuggestionsForTest extends PlatformSecrets {

  protected val directoryResolver = new PlatformDirectoryResolver(platform)

  def suggest(url: String, format: String, customDialect: Option[CustomDialects]): Future[Seq[Suggestion]] = {

    var position = 0
    for {
      _       <- Suggestions.init(InitOptions.AllProfiles.withCustomDialects(customDialect.toSeq))
      content <- platform.resolve(url)
      r       <- suggestFromFile(content.stream.toString, url, content.mime, format, customDialect)
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
                      format: String,
                      customDialect: Option[CustomDialects]): Future[Seq[Suggestion]] = {

    var position = 0
    for {
      _ <- Suggestions.init(InitOptions.AllProfiles.withCustomDialects(customDialect.toSeq))
      env <- Future.successful {
        val fileContentsStr = content
        val markerInfo      = this.findMarker(fileContentsStr)

        position = markerInfo.position

        this.buildEnvironment(url, markerInfo.originalContent, mime)
      }

      suggestions <- Suggestions.suggest(format, url, position, directoryResolver, env, platform)
    } yield suggestions.map(suggestion => suggestion)
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
      new MarkerInfo(str1, str1.length, str1)
    else {
      val rawContent = str1.replace(label, "")

      val preparedContent =
        org.mulesoft.als.suggestions.Core
          .prepareText(rawContent, position, YAML)
      new MarkerInfo(preparedContent, position, rawContent)
    }

  }
}

class MarkerInfo(val content: String, val position: Int, val originalContent: String) {}
