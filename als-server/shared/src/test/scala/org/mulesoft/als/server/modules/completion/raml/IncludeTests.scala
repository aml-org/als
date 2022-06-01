//package org.mulesoft.als.server.modules.completion.raml
//
//import scala.concurrent.ExecutionContext
//
//class IncludeTests extends RAMLSuggestionTestServer {
//
//  override implicit val executionContext = ExecutionContext.Implicits.global
//
//  test("test01") {
//    runTest(
//      "includes/testGroup01/test01.raml",
//      Set("fragments/test Fragment.raml", "fragments/testFragment.raml", "fragments/testFragment2.raml")
//    )
//  }
//
//  test("test02") {
//    runTest(
//      "includes/testGroup01/test02.raml",
//      Set("fragments/testFragment.raml", "fragments/test Fragment.raml", "fragments/testFragment2.raml")
//    )
//  }
//
//  test("test03") {
//    runTest("includes/testGroup01/test03.raml", Set("fragments/testFragment.raml"))
//  }
//
//  test("test04") {
//    runTest("includes/testGroup01/test04.raml", Set("fragments/"))
//  }
//
//  test("test05") {
//    runTest(
//      "includes/testGroup01/test 05.raml",
//      Set("fragments/test Fragment.raml", "fragments/testFragment.raml", "fragments/testFragment2.raml")
//    )
//  }
//
//  test("test06") {
//    runTest("includes/testGroup02/test01.raml", Set("test02.raml", "testFragment.raml", "testFragment2.raml"))
//  }
//
//  test("test07") {
//    runTest("includes/testGroup02/test02.raml", Set("testFragment.raml"))
//  }
//
//  test("test with % 1") {
//    runTest("includes/testGroup%5A/test%25.raml", Set("test%25 B.raml", "with space/"))
//  }
//
//  test("test with % 2") {
//    runTest("includes/testGroup%5A/test%25 B.raml", Set("with space/test%A5 lib.raml"))
//  }
//
//  test("test with '//'") {
//    runTest("includes/testGroup04/double-slash.raml", Set("//t.raml", "//double-slash.raml"))
//  }
//}
