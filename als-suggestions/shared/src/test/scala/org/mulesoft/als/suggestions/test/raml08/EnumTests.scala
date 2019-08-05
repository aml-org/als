package org.mulesoft.als.suggestions.test.raml08

class EnumTests extends RAML08Test {

  test("Security scheme types completion") {
    this.runSuggestionTest("enums/test001.raml",
                           Set("OAuth 1.0", "OAuth 2.0", "Basic Authentication", "Digest Authentication", "x-{other}"))
  }
}
