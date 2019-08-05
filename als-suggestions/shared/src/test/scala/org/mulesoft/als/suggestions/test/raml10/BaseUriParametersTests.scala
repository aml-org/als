package org.mulesoft.als.suggestions.test.raml10

class BaseUriParametersTests extends RAML10Test {
  test("Complete list of BaseUriParameters") {
    this.runSuggestionTest("base-uri-parameters/api1.raml", Set("projectId: ", "environment: "))
  }

  test("Filtered list of BaseUriParameters") {
    this.runSuggestionTest("base-uri-parameters/api2.raml", Set("environment: "))
  }
}
