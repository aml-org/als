package org.mulesoft.als.suggestions.client.jvm

import amf.client.remote.Content
import amf.core.remote.{FileNotFound, Raml10}
import amf.core.unsafe.PlatformSecrets
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfmanager.InitOptions
import org.scalatest.{AsyncFunSuite, Matchers}
import amf.internal.environment.Environment

import scala.concurrent.{ExecutionContext, Future}

class JvmSuggestionsTest extends AsyncFunSuite with Matchers with PlatformSecrets {
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
        try { new Content(textInput, resource) } catch {
          case e: Exception => throw FileNotFound(e)
        }
      })
  }

  val directoryResolver: DirectoryResolver = new DirectoryResolver {
    override def exists(path: String): Future[Boolean] = Future { path == url }

    override def readDir(path: String): Future[Seq[String]] = Future { Seq() }

    override def isDirectory(path: String): Future[Boolean] = Future { false }
  }

  val environment
    : Environment = Environment().add(fileLoader) // .add(new ResourceLoaderAdapter(new FileResourceLoader()))

  test("Custom Resource Loader test") {
    for {
      _ <- Suggestions.init(InitOptions.AllProfiles)
      suggestions <- Suggestions.suggest(Raml10.name,
                                         url,
                                         40,
                                         directoryResolver,
                                         environment,
                                         platform,
                                         snippetsSupport = true)
    } yield {
      assert(suggestions.size == 14)
    }
  }
}
