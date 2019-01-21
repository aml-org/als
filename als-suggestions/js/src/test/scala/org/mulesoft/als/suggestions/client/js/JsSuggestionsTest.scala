package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.{ClientResourceLoader, ResourceLoader}
import amf.core.remote.Vendor
import org.scalatest.AsyncFunSuite

import scala.concurrent.ExecutionContext
import scala.scalajs.js

class JsSuggestionsTest extends AsyncFunSuite {
  override implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  test("Basic suggestion on file") {
    val fileContent =
      """#%RAML 1.0
        |title: Project 2
        |
      """.stripMargin

    val fileLoader = js.use(new ResourceLoader {
      override def fetch(resource: String): js.Promise[Content] = js.Promise.resolve[Content](new Content(fileContent, resource))

      override def accepts(resource: String): Boolean = resource == "file:///api.raml"
    }).as[ClientResourceLoader]

    JsSuggestions.init().toFuture
      .flatMap(_ => {
        JsSuggestions.suggest(Vendor.RAML.name, "file:///api.raml", 11, js.Array(fileLoader)).toFuture
          .map(suggestions => assertResult(15)(suggestions.length))
      })
  }

  test("Basic suggestion on file root") {
    val fileContent =
      """#%RAML 1.0
        |title: Project 2
        |
      """.stripMargin

    val fileLoader = js.use(new ResourceLoader {
      override def fetch(resource: String): js.Promise[Content] = js.Promise.resolve[Content](new Content(fileContent, resource))

      override def accepts(resource: String): Boolean = resource == "file:///api.raml"
    }).as[ClientResourceLoader]

    JsSuggestions.init().toFuture
      .flatMap(_ => {
        JsSuggestions.suggest(Vendor.RAML.name, "file:///api.raml", 28, js.Array(fileLoader)).toFuture
          .map(suggestions => assertResult(15)(suggestions.length))
      })
  }
}
