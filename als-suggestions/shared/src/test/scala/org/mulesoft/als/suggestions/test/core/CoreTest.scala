package org.mulesoft.als.suggestions.test.core

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.{MarkerFinderTest, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.{ExecutionContext, Future}

trait CoreTest extends AsyncFunSuite with AlsPlatformSecrets with MarkerFinderTest with AccessBundle {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def rootPath: String

  def suggest(path: String, alsConfiguration: ALSConfigurationState): Future[Seq[CompletionItem]] = {

    val url = filePath(path)
    for {
      content <- alsConfiguration.fetchContent(url)
      (newConfiguration, position) <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr, "*")
        val newConfig = ALSConfigurationState(
          alsConfiguration.editorState,
          alsConfiguration.projectState,
          Some(resourceLoader(url, markerInfo.content))
        )

        (newConfig, markerInfo.offset)
      }
      suggestions <- {
        new Suggestions(
          AlsConfiguration(),
          new PlatformDirectoryResolver(newConfiguration.platform),
          accessBundle(newConfiguration)
        )
          .initialized()
          .suggest(url, position, snippetsSupport = true, None)
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

  def runTestForCustomDialect(
      path: String,
      dialectPath: String,
      originalSuggestions: Set[String]
  ): Future[Assertion] = {
    EditorConfiguration()
      .withDialect(filePath(dialectPath))
      .getState
      .flatMap(state => {
        val alsConfiguration = ALSConfigurationState(state, EmptyProjectConfigurationState, None)
        suggest(path, alsConfiguration).map(suggestions => {
          assert(suggestions.map(_.label).size == originalSuggestions.size)
          assert(suggestions.map(_.label).forall(s => originalSuggestions.contains(s)))
          assert(originalSuggestions.forall(s => suggestions.map(_.label).contains(s)))
        })
      })
  }

}
