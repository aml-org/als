package org.mulesoft.als.suggestions.test.raml10

class IncludeTests extends RAML10Test {

  test("test01") {
    this.runTest("includes/testGroup01/test01.raml", Set("/testFragment.raml", "/testFragment2.raml"))
  }

  test("test02") {
    this.runTest("includes/testGroup01/test02.raml", Set("testFragment.raml", "testFragment2.raml"))
  }

  test("test03") {
    this.runTest("includes/testGroup01/test03.raml", Set("raml"))
  }

  test("test04") {
    this.runTest("includes/testGroup01/test04.raml", Set("fragments/"))
  }

  test("test05") {
    this.runTest("includes/testGroup02/test01.raml",
                 Set("test02.raml", "test03.raml", "testFragment.raml", "testFragment2.raml"))
  }

  test("test06") {
    this.runTest("includes/testGroup02/test02.raml", Set("raml"))
  }

  test("test07") {
    this.runTest("includes/testGroup02/test03.raml",
                 Set("test01.raml", "test02.raml", "root", "testFragment.raml", "testFragment2.raml"))
  }

  test("test08") {
    this.runTest("includes/testGroup02/root",
                 Set("test01.raml", "test02.raml", "test03.raml", "testFragment.raml", "testFragment2.raml"))
  }

  test("test09") {
    this.runTest("includes/subdirs/rootsubdir/api.raml",
                 Set("fragments/", "include-sub2-dir.raml", "rootsubdir/", "include-sub-dir.raml"))
  }
}
