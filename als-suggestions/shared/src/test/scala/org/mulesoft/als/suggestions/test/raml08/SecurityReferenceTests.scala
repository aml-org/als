package org.mulesoft.als.suggestions.test.raml08

class SecurityReferenceTests extends RAML08Test {

  test("authorizationGrants test 001") {
    this.runSuggestionTest("security/test001.raml", Set("code", "token", "owner", "credentials"))
  }

  test("authorizationGrants test 002") {
    this.runSuggestionTest("security/test002.raml", Set("code", "token", "owner", "credentials"))
  }

  test("authorizationGrants test 003") {
    this.runSuggestionTest("security/test003.raml", Set("token", "owner", "credentials"))
  }

  test("authorizationGrants test 004") {
    this.runSuggestionTest("security/test004.raml", Set("code"))
  }

  test("Api securedBy test001") {
    this.runSuggestionTest("security/test005.raml", Set("obasic", "oauth2"))
  }

  test("Api securedBy test002") {
    this.runSuggestionTest("security/test006.raml", Set("obasic", "oauth2"))
  }

  test("Api securedBy test003") {
    this.runSuggestionTest("security/test007.raml", Set("obasic", "oauth2"))
  }

  test("Api securedBy test004") {
    this.runSuggestionTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Api securedBy test005") {
    this.runSuggestionTest("security/test009.raml", Set("oauth2"))
  }

  test("Api securedBy test006") {
    this.runSuggestionTest("security/test010.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test001") {
    this.runSuggestionTest("security/test005.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test002") {
    this.runSuggestionTest("security/test006.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test003") {
    this.runSuggestionTest("security/test007.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test004") {
    this.runSuggestionTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test005") {
    this.runSuggestionTest("security/test009.raml", Set("oauth2"))
  }

  test("Resource securedBy test006") {
    this.runSuggestionTest("security/test010.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test001") {
    this.runSuggestionTest("security/test005.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test002") {
    this.runSuggestionTest("security/test006.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test003") {
    this.runSuggestionTest("security/test007.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test004") {
    this.runSuggestionTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test005") {
    this.runSuggestionTest("security/test009.raml", Set("oauth2"))
  }

  test("Method securedBy test006") {
    this.runSuggestionTest("security/test010.raml", Set("obasic", "oauth2"))
  }

}
