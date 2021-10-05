package org.mulesoft.als.suggestions.test.core

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.{MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

trait CoreTest extends AsyncFunSuite with PlatformSecrets with MarkerFinderTest {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def rootPath: String

  def suggest(path: String, amfConfiguration: AmfConfigurationWrapper): Future[Seq[CompletionItem]] = {

    val url = filePath(path)
    for {
      content <- amfConfiguration.fetchContent(url)
      position <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr, "*")
        amfConfiguration.withResourceLoader(this.resourceLoader(url, markerInfo.content))
        markerInfo.offset
      }
      suggestions <- {
        new Suggestions(AlsConfiguration(), new PlatformDirectoryResolver(amfConfiguration.platform))
          .initialized()
          .suggest(url, position, snippetsSupport = true, None, amfConfiguration)
      }
    } yield suggestions
  }

  def filePath(path: String): String =
    s"file://als-suggestions/shared/src/test/resources/test/$rootPath/$path"
      .replace('\\', '/')
      .replace("/null", "")

  def resourceLoader(fileUrl: String, content: String): ResourceLoader = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == fileUrl

    override def fetch(resource: String): Future[Content] =
      Future.successful(new Content(content, fileUrl))
  }

  def runTestForCustomDialect(path: String, dialectPath: String, originalSuggestions: Set[String]): Future[Assertion] = {
    val p                = filePath(dialectPath)
    val amfConfiguration = AmfConfigurationWrapper()
    amfConfiguration
      .parse(p)
      .map { r =>
        r.result.baseUnit match {
          case d: Dialect => amfConfiguration.registerDialect(d)
        }
        r
      }
      .flatMap(_ =>
        suggest(path, amfConfiguration).map(suggestions => {
          assert(suggestions.map(_.label).size == originalSuggestions.size)
          assert(suggestions.map(_.label).forall(s => originalSuggestions.contains(s)))
          assert(originalSuggestions.forall(s => suggestions.map(_.label).contains(s)))
        }))
  }
}
