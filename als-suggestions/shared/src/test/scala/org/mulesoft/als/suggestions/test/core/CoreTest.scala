package org.mulesoft.als.suggestions.test.core

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.client.{Suggestion, SuggestionsAST}
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.{BaseCompletionPluginsRegistryAML, Core}
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.Future

trait CoreTest extends AsyncFunSuite with PlatformSecrets {
  private val directoryResolver = new PlatformDirectoryResolver(platform)

  def format: String

  def rootPath: String

  def suggest(path: String,
              plugins: Seq[CompletionPlugin],
              dialects: Seq[Dialect] = Nil,
              label: String = "*",
              cutTail: Boolean = false,
              labels: Array[String] = Array("*")): Future[Seq[Suggestion]] = {

    var position: Position = Position(0, 0)
    val url                = filePath(path)

    SuggestionsAST.init(plugins, dialects)
    for {
      content <- platform.resolve(url)
      env <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        position = Position(markerInfo.position, markerInfo.content)

        this.buildEnvironment(url, markerInfo.originalContent, content.mime)
      }

      suggestions <- SuggestionsAST.suggest(format, url, position, directoryResolver, env, platform)
    } yield suggestions.map(suggestion => suggestion)
  }

  def filePath(path: String): String =
    s"file://als-suggestions/shared/src/test/resources/test/$rootPath/$path"
      .replace('\\', '/')
      .replace("/null", "")

  def findMarker(str: String,
                 label: String = "*",
                 cut: Boolean = false,
                 labels: Array[String] = Array("*")): MarkerInfo = {
    val position = str.indexOf(label)

    val str1 = {
      if (cut && position >= 0) {
        str.substring(0, position)
      } else {
        str
      }
    }

    if (position < 0) {
      new MarkerInfo(str1, str1.length, str1)
    } else {
      val rawContent = str1.replace(label, "")

      val preparedContent =
        Core.prepareText(rawContent, position, YAML)
      new MarkerInfo(preparedContent, position, rawContent)
    }
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

  def runTestForCustomDialect(
      path: String,
      dialectPath: String,
      originalSuggestions: Set[String],
      plugins: Seq[CompletionPlugin] = BaseCompletionPluginsRegistryAML.get()): Future[Assertion] =
    parseAMF(filePath(dialectPath)).flatMap(_ =>
      suggest(path, plugins).map(suggestions => {
        assert(suggestions.map(_.displayText).size == originalSuggestions.size)
        assert(suggestions.map(_.displayText).forall(s => originalSuggestions.contains(s)))
        assert(originalSuggestions.forall(s => suggestions.map(_.displayText).contains(s)))
      }))

  def parseAMF(path: String, env: Environment = Environment()): Future[BaseUnit] = {
    val cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(path),
      Some(format),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val helper = ParserHelper(platform)
    helper.parse(cfg, env)
  }
}

class MarkerInfo(val content: String, val position: Int, val originalContent: String) {}
