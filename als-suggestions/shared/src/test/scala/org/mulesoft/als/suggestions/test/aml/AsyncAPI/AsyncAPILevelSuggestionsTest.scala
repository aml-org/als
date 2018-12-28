package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import amf.core.remote.Aml
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.suggestions.test.aml.DialectLevelSuggestionsTest

class AsyncAPILevelSuggestionsTest extends DialectLevelSuggestionsTest {

  test("Test test") {
    runDialectTest("test.yaml", "file:///dialect6.yaml")
  }
// todo add test for a prop inside asyncapiobject (levl2) for example

  // add different ssuit for oas with dialects??
  override def format: String = Aml.toString

  override def rootPath: String = "AML/AsyncAPI/full"
}
