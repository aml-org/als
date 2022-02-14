package org.mulesoft.als.suggestions.test.aml

import org.mulesoft.als.suggestions.test.SuggestionsTest
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class AMLSuggestionsTest extends SuggestionsTest {

  def runTestForCustomDialect(path: String, dialectPath: String, originalSuggestions: Set[String]): Future[Assertion] = {
    parseAMF(filePath(dialectPath)).flatMap(_ => runSuggestionTest(filePath(path), originalSuggestions))
  }
}
