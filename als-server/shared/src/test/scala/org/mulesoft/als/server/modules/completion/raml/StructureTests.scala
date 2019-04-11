package org.mulesoft.als.server.modules.completion.raml

class StructureTests extends RAMLSuggestionTestServer {

  test("test 01") {
    runTest("structure/test01.raml", Set("responses:\n      "))
  }

  // TODO: tests are failing when "test 01" doesn't run first"
  //  for example: "serverJVM/testOnly *StructureTests* -- -z "test 02"" fails,
  //  while "serverJVM/testOnly *StructureTests* -- -z "test 01"" runs without problems
  test("test 02") {
    runTest("structure/test02.raml", Set("types:\n  "))
  }

  test("test 03") {
    runTest("structure/test03.raml", Set("resourceTypes"))
  }

  test("test 04") {
    runTest("structure/test04.raml", Set("title: "))
  }

  test("test 05") {
    runTest("structure/test05.raml", Set("traits:\n  "))
  }

  test("test 06") {
    runTest("structure/test06.raml", Set("description: "))
  }

  test("test 07") {
    runTest("structure/test07.raml", Set("documentation:\n  "))
  }

  test("test 08") {
    runTest("structure/test08.raml", Set("version: "))
  }

  test("test 09") {
    runTest("structure/test09.raml", Set("baseUri: ", "baseUriParameters:\n  "))
  }

  test("test 10") {
    runTest("structure/test10.raml", Set("protocols: "))
  }

  ignore("test with spaces") { // TODO: How to test in JS?
    runTest("structure/with spaces/root test.raml", Set("lib test.raml"))
  }

  test("test empty root") {
    runTest("testEmpty.raml", Set("#%RAML 1.0", "#%RAML 0.8", "swagger: '2.0'"))
  }
}
