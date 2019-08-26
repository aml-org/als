package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.{ClientResourceLoader, ResourceLoader}
import amf.core.remote.Vendor
import org.mulesoft.als.common.{DirectoryResolver => InternalDirectoryResolver}
import org.scalatest.{Assertions, AsyncFunSuite, Matchers}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class JsSuggestionsTest extends AsyncFunSuite with Matchers {
  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  test("Basic suggestion on file") {
    val fileContent =
      "#%RAML 1.0\ntitle: Project 2\n\n"

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] =
          js.Promise.resolve[Content](new Content(fileContent, resource))

        override def accepts(resource: String): Boolean =
          resource == "file:///api.raml"
      })
      .as[ClientResourceLoader]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///api.raml", 28, js.Array(fileLoader))
          .toFuture
          .map(suggestions => assertResult(14)(suggestions.length))
      })
  }

  test("Basic suggestion on file root") {
    val fileContent =
      "#%RAML 1.0\ntitle: Project 2\n\n"

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] =
          js.Promise.resolve[Content](new Content(fileContent, resource))

        override def accepts(resource: String): Boolean =
          resource == "file:///api.raml"
      })
      .as[ClientResourceLoader]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///api.raml", 28, js.Array(fileLoader))
          .toFuture
          .map(suggestions => assertResult(14)(suggestions.length))
      })
  }

  test("Custom Directory Resolver") {
    val api =
      "#%RAML 1.0\ntitle: Project 2\ntraits:\n  t: !include  \ntypes:\n  a: string"

    val fragment =
      """#%RAML 1.0 Trait
        |responses:
        | 200:
      """.stripMargin.replace("\r\n", "\n")

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] = {
          if (resource == "file:///dir/api.raml")
            js.Promise.resolve[Content](new Content(api, resource))
          else
            js.Promise.resolve[Content](new Content(fragment, resource))
        }

        override def accepts(resource: String): Boolean =
          resource == "file:///dir/api.raml" || resource == "file://dir/fragment%202.raml"
      })
      .as[ClientResourceLoader]

    val clientResolver = js
      .use(new ClientDirectoryResolver {
        override def exists(path: String): js.Promise[Boolean] =
          Future(Seq("/api.raml", "fragment%202.raml", "another.raml").contains(path)).toJSPromise

        override def readDir(path: String): js.Promise[js.Array[String]] =
          Future(Seq("/fragment 2.raml", "/another.raml"))
            .map(_.toJSArray)
            .toJSPromise

        override def isDirectory(path: String): js.Promise[Boolean] =
          Future(path endsWith "dir/").toJSPromise
      })
      .as[ClientDirectoryResolver]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///dir/api.raml", 51, js.Array(fileLoader), clientResolver)
          .toFuture
          .map(suggestions => {
            val seq = suggestions.toSeq
            seq.size should be(2)
            seq.head.text should be("/fragment 2.raml")
            seq.last.text should be("/another.raml")
          })
      })
  }

  test("Custom Directory Resolver Root") {
    val api =
      "#%RAML 1.0\ntitle: Project 2\ntraits:\n  t: !include  \ntypes:\n  a: string"

    val fragment =
      """#%RAML 1.0 Trait
        |responses:
        | 200:
      """.stripMargin.replace("\r\n", "\n")

    val fileLoader = js
      .use(new ResourceLoader {
        override def fetch(resource: String): js.Promise[Content] = {
          if (resource == "file:///api.raml")
            js.Promise.resolve[Content](new Content(api, resource))
          else
            js.Promise.resolve[Content](new Content(fragment, resource))
        }

        override def accepts(resource: String): Boolean =
          resource == "file:///api.raml" || resource == "file://fragment%202.raml"
      })
      .as[ClientResourceLoader]

    val clientResolver = js
      .use(new ClientDirectoryResolver {
        override def exists(path: String): js.Promise[Boolean] =
          Future(Seq("api.raml", "fragment%202.raml", "another.raml").contains(path)).toJSPromise

        override def readDir(path: String): js.Promise[js.Array[String]] =
          Future(Seq("fragment 2.raml", "another.raml"))
            .map(_.toJSArray)
            .toJSPromise

        override def isDirectory(path: String): js.Promise[Boolean] =
          Future(!path.endsWith(".raml")).toJSPromise
      })
      .as[ClientDirectoryResolver]

    JsSuggestions
      .init()
      .toFuture
      .flatMap(_ => {
        JsSuggestions
          .suggest(Vendor.RAML.name, "file:///api.raml", 51, js.Array(fileLoader), clientResolver)
          .toFuture
          .map(suggestions => {
            val seq = suggestions.toSeq
            seq.size should be(2)
            seq.head.text should be("fragment 2.raml")
            seq.last.text should be("another.raml")
          })
      })
  }
}
