package org.mulesoft.als.suggestions.test.raml08

class SecurityReferenceTests extends RAML08Test {

  test("authorizationGrants test 001") {
    this.runTest("security/test001.raml", Set("[ code ]", "[ token ]", "[ owner ]", "[ credentials ]"))
  }

  test("authorizationGrants test 002") {
    this.runTest("security/test002.raml", Set("code", "token", "owner", "credentials"))
  }

  test("authorizationGrants test 003") {
    this.runTest("security/test003.raml", Set("token", "owner", "credentials"))
  }

  test("authorizationGrants test 004") {
    this.runTest("security/test004.raml", Set("code"))
  }

  test("Api securedBy test001") {
    this.runTest("security/test005.raml", Set("[ obasic ]", "[ oauth2 ]", "[ null ]"))
  }

  test("Api securedBy test002") {
    this.runTest("security/test006.raml", Set("[ obasic ]", "[ oauth2 ]"))
  }

  test("Api securedBy test003") {
    this.runTest("security/test007.raml", Set("obasic", "oauth2", "null"))
  }

  test("Api securedBy test004") {
    this.runTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Api securedBy test005") {
    this.runTest("security/test009.raml", Set("null", "oauth2"))
  }

  test("Api securedBy test006") {
    this.runTest("security/test010.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test001") {
    this.runTest("security/test005.raml", Set("[ obasic ]", "[ oauth2 ]", "[ null ]"))
  }

  test("Resource securedBy test002") {
    this.runTest("security/test006.raml", Set("[ obasic ]", "[ oauth2 ]"))
  }

  test("Resource securedBy test003") {
    this.runTest("security/test007.raml", Set("obasic", "oauth2", "null"))
  }

  test("Resource securedBy test004") {
    this.runTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Resource securedBy test005") {
    this.runTest("security/test009.raml", Set("null", "oauth2"))
  }

  test("Resource securedBy test006") {
    this.runTest("security/test010.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test001") {
    this.runTest("security/test005.raml", Set("[ obasic ]", "[ oauth2 ]", "[ null ]"))
  }

  test("Method securedBy test002") {
    this.runTest("security/test006.raml", Set("[ obasic ]", "[ oauth2 ]"))
  }

  test("Method securedBy test003") {
    this.runTest("security/test007.raml", Set("obasic", "oauth2", "null"))
  }

  test("Method securedBy test004") {
    this.runTest("security/test008.raml", Set("obasic", "oauth2"))
  }

  test("Method securedBy test005") {
    this.runTest("security/test009.raml", Set("null", "oauth2"))
  }

  test("Method securedBy test006") {
    this.runTest("security/test010.raml", Set("obasic", "oauth2"))
  }

}
