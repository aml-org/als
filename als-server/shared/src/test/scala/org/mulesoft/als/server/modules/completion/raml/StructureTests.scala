//package org.mulesoft.als.server.modules.completion.raml
//
//import scala.concurrent.ExecutionContext
//
//class StructureTests extends RAMLSuggestionTestServer {
//
//  override implicit val executionContext = ExecutionContext.Implicits.global
//
//  test("test 01") {
//    runTest("structure/test01.raml", Set("responses:\n  "))
//  }
//
//  test("test 02") {
//    runTest("structure/test02.raml", Set("types:\n  "))
//  }
//
//  test("test 03") {
//    runTest("structure/test03.raml", Set("resourceTypes:\n  "))
//  }
//
//  test("test 04") {
//    runTest("structure/test04.raml", Set("title: "))
//  }
//
//  test("test 05") {
//    runTest("structure/test05.raml", Set("traits:\n  "))
//  }
//
//  test("test 06") {
//    runTest("structure/test06.raml", Set("description: "))
//  }
//
//  test("test 07") {
//    runTest(
//      "structure/test07.raml",
//      Set(
//        "documentation:\n  - ",
//        """documentation:
//           |  -
//           |    content: $1
//           |    title: $2""".stripMargin
//      )
//    )
//  }
//
//  test("test 08") {
//    runTest("structure/test08.raml", Set("version: "))
//  }
//
//  test("test 09") {
//    runTest("structure/test09.raml", Set("baseUri: ", "baseUriParameters:\n  "))
//  }
//
//  test("test 10") {
//    runTest("structure/test10.raml", Set("protocols:\n  - "))
//  }
//
//  test("test with spaces") { // TODO: How to test in JS?
//    runTest("structure/with spaces/root test.raml", Set("lib test.raml"))
//  }
//
//  test("test empty root") {
//    runTest("testEmpty.raml", Set("#%RAML 1.0"))
//  }
//}
