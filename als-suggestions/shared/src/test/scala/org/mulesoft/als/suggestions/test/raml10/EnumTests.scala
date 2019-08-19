package org.mulesoft.als.suggestions.test.raml10

class EnumTests extends RAML10Test {

  test("Security scheme types completion") {
    this.runSuggestionTest(
      "enums/test001.raml",
      Set("OAuth 1.0", "OAuth 2.0", "Basic Authentication", "Digest Authentication", "Pass Through", "x-"))
  }

  test("NumberType format completion") {
    this.runSuggestionTest("enums/test002.raml",
                           Set("int32", "int64", "int", "long", "float", "double", "int16", "int8"))
  }
}
