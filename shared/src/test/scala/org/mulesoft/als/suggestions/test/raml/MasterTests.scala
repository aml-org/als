package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class MasterTests extends RAMLTest {

  test("test01") {
    this.runTest("masterRef/testGroup01/test01.raml",
      Set("/testMaster.raml","/testMaster2.raml"))
  }

  test("test02") {
    this.runTest("masterRef/testGroup01/test02.raml",
      Set("testMaster.raml","testMaster2.raml"))
  }

  test("test03") {
    this.runTest("masterRef/testGroup01/test03.raml",
      Set("raml"))
  }

  test("test04") {
    this.runTest("masterRef/testGroup01/test04.raml",
      Set("masters"))
  }

  test("test05") {
    this.runTest("masterRef/testGroup02/test01.raml",
      Set("test01.raml", "test02.raml" ,"testMaster.raml","testMaster2.raml"))
  }

  test("test06") {
    this.runTest("masterRef/testGroup02/test02.raml",
      Set("raml"))
  }
}
