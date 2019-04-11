package org.mulesoft.als.server.modules.completion.raml

class IncludeTests extends RAMLSuggestionTestServer {

  ignore("test01") {
    runTest("includes/testGroup01/test01.raml", Set("testFragment.raml", "testFragment2.raml"))
  }

  ignore("test02") {
    runTest("includes/testGroup01/test02.raml", Set("testFragment.raml", "testFragment2.raml"))
  }

  ignore("test03") {
    runTest("includes/testGroup01/test03.raml", Set("raml"))
  }

  ignore("test04") {
    runTest("includes/testGroup01/test04.raml", Set("fragments/"))
  }

  ignore("test05") {
    runTest("includes/testGroup02/test01.raml", Set("test02.raml", "testFragment.raml", "testFragment2.raml"))
  }

  ignore("test06") {
    runTest("includes/testGroup02/test02.raml", Set("raml"))
  }
}
