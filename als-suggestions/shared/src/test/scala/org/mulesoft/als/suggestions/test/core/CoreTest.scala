package org.mulesoft.als.suggestions.test.core

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfmanager.InitOptions
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.mulesoft.lsp.server.DefaultAmfConfiguration
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

trait CoreTest extends AsyncFunSuite with PlatformSecrets {
  private val directoryResolver = new PlatformDirectoryResolver(platform)

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def rootPath: String

  def suggest(path: String,
              label: String = "*",
              cutTail: Boolean = false,
              labels: Array[String] = Array("*"),
              suggestions: Suggestions): Future[Seq[CompletionItem]] = {

    val url = filePath(path)
    for {
      content <- platform.resolve(url)
      (env, position) <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        (this.buildEnvironment(url, markerInfo.patchedContent.original, content.mime), markerInfo.position)
      }

      suggestions <- suggestions.suggest(url, position, snippetsSupport = true)
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
      new MarkerInfo(PatchedContent(str1, str, Nil), str1.length)
    } else {
      val rawContent = str1.replace(label, "")
      val preparedContent =
        ContentPatcher(rawContent, position, YAML).prepareContent()
      new MarkerInfo(preparedContent, position)
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
    val p             = filePath(dialectPath)
    val configuration = DefaultAmfConfiguration
    val s             = new Suggestions(configuration)
    s.init(InitOptions.AllProfiles)
      .flatMap(_ => configuration.parse(p))
      .flatMap(_ =>
        suggest(path, suggestions = s).map(suggestions => {
          AMLPlugin.registry.remove(p)
          assert(suggestions.map(_.label).size == originalSuggestions.size)
          assert(suggestions.map(_.label).forall(s => originalSuggestions.contains(s)))
          assert(originalSuggestions.forall(s => suggestions.map(_.label).contains(s)))
        }))
  }
}

class MarkerInfo(val patchedContent: PatchedContent, val position: Int) {}
