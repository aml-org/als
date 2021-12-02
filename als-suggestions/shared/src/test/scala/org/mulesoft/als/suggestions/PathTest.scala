package org.mulesoft.als.suggestions

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.FileNotFound
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.suggestions.plugins.aml.AMLPathCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.scalatest.AsyncFunSuite
import org.scalatest.compatible.Assertion

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PathTest extends AsyncFunSuite with PlatformSecrets {

  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val urlDir = "file:///absolute/path/"
  val url    = s"${urlDir}api.raml"
  val urlSub = s"${urlDir}directory/lib.raml"

  val fileLoader: ResourceLoader = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == url

    override def fetch(resource: String): Future[Content] =
      Future.successful({
        try {
          new Content("", resource)
        } catch {
          case e: Exception => throw FileNotFound(e)
        }
      })
  }

  val directoryResolver: DirectoryResolver = new DirectoryResolver {
    private val directories = Seq("directory")
    private val files       = Seq("api.raml")
    private val subDir      = Seq("lib.raml", "subdir")
    private val all         = files ++ directories

    override def exists(path: String): Future[Boolean] = Future.successful(
      (all ++ subDir.map(sd => s"${urlDir}directory/$sd")).contains(path)
    )

    override def readDir(path: String): Future[Seq[String]] = Future.successful(
      if (path == urlDir) all
      else if (path == s"${urlDir}directory") subDir
      else Nil
    )

    override def isDirectory(path: String): Future[Boolean] =
      Future.successful(!path.contains('.'))
  }
  val futureAlsConfiguration: Future[ALSConfigurationState] = {
    EditorConfiguration
      .withPlatformLoaders(Seq(fileLoader))
      .getState
      .map(editorState => {
        ALSConfigurationState(editorState = editorState,
                              projectState = EmptyProjectConfigurationState,
                              editorResourceLoader = None)
      })
  }

  test("Should list files from absolute route, having '/' prefix") {
    val eventualAssertion: Future[Assertion] = for {
      alsConfiguration <- futureAlsConfiguration
      result           <- AMLPathCompletionPlugin.resolveInclusion(url, directoryResolver, "/", None, alsConfiguration)
    } yield {
      assert(result.size == 1)
    }
    eventualAssertion
  }

  test("Should list files from absolute route, NOT having '/' prefix") {
    for {
      alsConfiguration <- futureAlsConfiguration
      result           <- AMLPathCompletionPlugin.resolveInclusion(url, directoryResolver, "", None, alsConfiguration)
    } yield {
      assert(result.size == 1)
    }
  }

  test("Should list files from root route, having '/' prefix") {
    for {
      alsConfiguration <- futureAlsConfiguration
      result           <- AMLPathCompletionPlugin.resolveInclusion(url, directoryResolver, "/", Some(urlDir), alsConfiguration)
    } yield {
      assert(result.forall(r => Seq("/api.raml", "/directory/").contains(r.newText)))
    }
  }
}
