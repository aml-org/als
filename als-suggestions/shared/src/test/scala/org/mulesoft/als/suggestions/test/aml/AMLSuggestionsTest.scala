package org.mulesoft.als.suggestions.test.aml

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.SuggestionsTest
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class AMLSuggestionsTest extends SuggestionsTest {

  def format: String = Aml.toString

  def runTestForCustomDialect(path: String, dialectPath: String, originalSuggestions: Set[String]): Future[Assertion] = {
    parseAMF(filePath(dialectPath)).flatMap(_ => runTest(path, originalSuggestions))
  }

}
