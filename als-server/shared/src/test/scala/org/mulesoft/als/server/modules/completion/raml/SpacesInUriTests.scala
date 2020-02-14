package org.mulesoft.als.server.modules.completion.raml

import scala.concurrent.ExecutionContext

class SpacesInUriTests extends RAMLSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("test01") {
    runTest("spaces in folder/api.raml", Set("responses:\n      "))
  }
}
