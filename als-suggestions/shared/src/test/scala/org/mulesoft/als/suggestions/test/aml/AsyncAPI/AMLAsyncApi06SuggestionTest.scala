package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class AMLAsyncApi06SuggestionTest extends AMLSuggestionsTest {

  def runAsyncApiTest(path: String, originalSuggestions: Set[String]): Future[Assertion] = {
    runSuggestionTest(filePath(path), originalSuggestions, dialect = Some(filePath("dialect6.yaml")))
  }

}
