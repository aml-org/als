package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.{ClientResourceLoader, ResourceLoader}
import amf.core.remote.Vendor
import org.mulesoft.high.level.interfaces.{DirectoryResolver => InternalDirectoryResolver}
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class JsSuggestionsTest extends AsyncFunSuite with Matchers {
  override implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  test("Basic suggestion on file") {
    val fileContent =
      """#%RAML 1.0
        |title: Project 2
        |
      """.stripMargin

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] =
          js.Promise.resolve[Content](new Content(fileContent, resource))

        override def accepts(resource: String): Boolean = resource == "file:///api.raml"
      })
      .as[ClientResourceLoader]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///api.raml", 11, js.Array(fileLoader))
          .toFuture
          .map(suggestions => assertResult(15)(suggestions.length))
      })
  }

  test("Basic suggestion on file root") {
    val fileContent =
      """#%RAML 1.0
        |title: Project 2
        |
      """.stripMargin

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] =
          js.Promise.resolve[Content](new Content(fileContent, resource))

        override def accepts(resource: String): Boolean = resource == "file:///api.raml"
      })
      .as[ClientResourceLoader]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///api.raml", 28, js.Array(fileLoader))
          .toFuture
          .map(suggestions => assertResult(15)(suggestions.length))
      })
  }

  test("Custom Directory Resolver") {
    val api = "#%RAML 1.0\ntitle: Project 2\ntraits:\n  t: !include  \ntypes:\n  a: string"

    val fragment =
      """#%RAML 1.0 Trait
        |responses:
        | 200:
      """.stripMargin

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] = {
          if (resource == "file:///dir/api.raml")
            js.Promise.resolve[Content](new Content(api, resource))
          else
            js.Promise.resolve[Content](new Content(fragment, resource))
        }

        override def accepts(resource: String): Boolean =
          resource == "file:///dir/api.raml" || resource == "file://dir/fragment.raml"
      })
      .as[ClientResourceLoader]

    val internalResolver = new InternalDirectoryResolver {
      override def exists(path: String): Future[Boolean] =
        Future(Seq("file:///api.raml", "file://fragment.raml", "file://another.raml").contains(path))

      override def readDir(path: String): Future[Seq[String]] = {
        Future(Seq("file:///dir/fragment.raml", "file://dir/another.raml"))
      }

      override def isDirectory(path: String): Future[Boolean] = {
        Future(path endsWith "dir/")
      }
    }

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name,
                   "file:///dir/api.raml",
                   51,
                   js.Array(fileLoader),
                   Some(DirectoryResolverAdapter.asClient(internalResolver)).orUndefined)
          .toFuture
          .map(suggestions => {
            val seq = suggestions.toSeq
            seq.size should be(2)
            seq.head.text should be("fragment.raml")
            seq.last.text should be("another.raml")
          })
      })
  }
}
