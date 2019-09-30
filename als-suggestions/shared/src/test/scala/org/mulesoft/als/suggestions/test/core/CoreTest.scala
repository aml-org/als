package org.mulesoft.als.suggestions.test.core

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.suggestions.Core
import org.mulesoft.als.suggestions.client.{Suggestion, Suggestions}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.amfmanager.{InitOptions, ParserHelper}
import org.mulesoft.lsp.feature.completion.CompletionItem\
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

trait CoreTest extends AsyncFunSuite with PlatformSecrets {
  private val directoryResolver = new PlatformDirectoryResolver(platform)

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def format: String

  def rootPath: String

  def suggest(path: String,
              label: String = "*",
              cutTail: Boolean = false,
              labels: Array[String] = Array("*")): Future[Seq[CompletionItem]] = {

    val url = filePath(path)
    for {
      content <- platform.resolve(url)
      (env, position) <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        (this.buildEnvironment(url, markerInfo.originalContent, content.mime), markerInfo.position)
      }

      suggestions <- Suggestions.suggest(format,
                                         url,
                                         position,
                                         directoryResolver,
                                         env,
                                         platform,
                                         snippetsSupport = true)
    } yield suggestions
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

  def runTestForCustomDialect(path: String, dialectPath: String, originalSuggestions: Set[String]): Future[Assertion] = {
    val p = filePath(dialectPath)
    Suggestions
      .init(InitOptions.AllProfiles)
      .flatMap(_ => parseAMF(p))
      .flatMap(_ =>
        suggest(path).map(suggestions => {
          AMLPlugin.registry.remove(p)
          assert(suggestions.map(_.label).size == originalSuggestions.size)
          assert(suggestions.map(_.label).forall(s => originalSuggestions.contains(s)))
          assert(originalSuggestions.forall(s => suggestions.map(_.label).contains(s)))
        }))
  }

  def parseAMF(path: String, env: Environment = Environment()): Future[BaseUnit] = {

    val helper = ParserHelper(platform)
    helper.parse(path, env)
  }
}

class MarkerInfo(val content: String, val position: Int, val originalContent: String) {}
