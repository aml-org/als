package org.mulesoft.als.suggestions.test.raml10

class ProtocolsTests extends RAML10Test {

  test("Protocols test 01") {
    this.runSuggestionTest("protocols/test01.raml", Set("[ HTTP ]", "[ HTTPS ]"))
  }

  test("Protocols test 02") {
    this.runSuggestionTest("protocols/test02.raml", Set("[ HTTP ]", "[ HTTPS ]"))
  }

  test("Protocols test 03") {
    this.runSuggestionTest("protocols/test03.raml", Set("HTTP", "HTTPS"))
  }

  test("Protocols test 04") {
    this.runSuggestionTest("protocols/test04.raml", Set("HTTP", "HTTPS"))
  }

  test("Protocols test 05") {
    this.runSuggestionTest("protocols/test05.raml", Set("HTTP"))
  }

  test("Protocols test 06") {
    this.runSuggestionTest("protocols/test06.raml", Set("HTTP"))
  }

  //    test("Protocols test 07") {
  //        this.runTest("protocols/test07.raml", Set("HTTP"))
  //    }

  test("Protocols test 08") {
    this.runSuggestionTest("protocols/test08.raml", Set("HTTP"))
  }

  test("Protocols test 09") {
    this.runSuggestionTest("protocols/test09.raml", Set("HTTP"))
  }

  test("Protocols test 10") {
    this.runSuggestionTest("protocols/test10.raml", Set("[ HTTP ]", "[ HTTPS ]"))
  }
}
