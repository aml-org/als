package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class UsesTests extends RAMLTest {

  test("test01") {
    this.runTest("uses/testGroup01/test01.raml",
      Set("/testLib.raml","/testLib2.raml"))
  }

  test("test02") {
    this.runTest("uses/testGroup01/test02.raml",
      Set("testLib.raml","testLib2.raml"))
  }

  test("test03") {
    this.runTest("uses/testGroup01/test03.raml",
      Set("raml"))
  }

  test("test04") {
    this.runTest("uses/testGroup01/test04.raml",
      Set("libraries"))
  }

  test("test05") {
    this.runTest("uses/testGroup02/test01.raml",
      Set("test01.raml", "test02.raml" ,"testLib.raml","testLib2.raml"))
  }

  test("test06") {
    this.runTest("uses/testGroup02/test02.raml",
      Set("raml"))
  }
}
