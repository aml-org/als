package org.mulesoft.als.suggestions.client.jvm

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.FileNotFound
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.AccessBundle
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class JvmSuggestionsTest extends AsyncFunSuite with Matchers with AlsPlatformSecrets with AccessBundle {
  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
  // TODO: AMF is having trouble with "file:/" prefix (it transforms it in "file:///")
  val url = "file:///absolute/path/api.raml"

  val textInput: String =
    """#%RAML 1.0
      |title: ApiWithDependencies10
      |""".stripMargin.replaceAllLiterally("\r\n", "\n")

  val fileLoader: ResourceLoader = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == url

    override def fetch(resource: String): Future[Content] =
      Future[Content]({
        try { new Content(textInput, resource) }
        catch {
          case e: Exception => throw FileNotFound(e)
        }
      })
  }

  val directoryResolver: DirectoryResolver = new DirectoryResolver {
    override def exists(path: String): Future[Boolean] = Future { path == url }

    override def readDir(path: String): Future[Seq[String]] = Future { Seq() }

    override def isDirectory(path: String): Future[Boolean] = Future { false }
  }

  val alsConfiguration: Future[ALSConfigurationState] = {
    EditorConfiguration
      .withPlatformLoaders(Seq(fileLoader))
      .getState
      .map(editorState => {
        ALSConfigurationState(
          editorState = editorState,
          projectState = EmptyProjectConfigurationState,
          editorResourceLoader = None
        )
      })
  }

  test("Custom Resource Loader test") {
    for {
      alsConfigurationState <- alsConfiguration
      s <- Future {
        new Suggestions(AlsConfiguration(), directoryResolver, accessBundle(alsConfigurationState))
          .initialized()
      }
      suggestions <- s.suggest(url, 40, snippetsSupport = true, None)
    } yield {
      assert(suggestions.size == 15)
    }
  }
}
