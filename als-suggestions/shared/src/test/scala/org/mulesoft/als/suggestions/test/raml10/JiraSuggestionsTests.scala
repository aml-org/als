package org.mulesoft.als.suggestions.test.raml10

class JiraSuggestionsTests extends RAML10Test {

  test("ALS-707 Single") {
    this.runTest("jira-tests/als-707/als-707a.raml",
                 Set("application/json", "application/xml", "application/x-www-form-urlencoded"))
  }

  test("ALS-707 Sequence") {
    this.runTest("jira-tests/als-707/als-707b.raml", Set("application/json", "application/x-www-form-urlencoded"))
  }

  test("ALS-707 Open Sequence") {
    this.runTest("jira-tests/als-707/als-707c.raml",
                 Set("application/json", "application/x-www-form-urlencoded", "application/xml"))
  }

  test("ALS-715 Suggestions after '/' character in composed words 01") {
    this.runTest("jira-tests/als-715/test01.raml", Set("json:\n              "))
  }

  test("ALS-715 Suggestions after '/' character in composed words 02") {
    this.runTest(
      "jira-tests/als-715/test02.raml",
      Set(
        "application/json:\n            ",
        "application/xml:\n            ",
        "example:\n            ",
        "properties:\n            ",
        "xml:\n            ",
        "facets:\n            ",
        "examples:\n            ",
        "description: ",
        "displayName: ",
        "type: ",
        "enum: ",
        "schema: ",
        "default: "
      )
    )
  }

  test("ALS-715 Suggestions after '/' character in composed words 03") {
    this.runTest("jira-tests/als-715/test03.raml", Set("json:\n              ", "xml:\n              "))
  }

  test("ALS-715 Suggestions after '/' character in composed words 04") {
    this.runTest("jira-tests/als-715/test04.raml", Set())
  }

  test("ALS-718 Library Suggestions") {
    this.runTest("jira-tests/als-718/api.raml", Set("Employee", "Person", "Manager"))
  }

  test("ALS-719 Library Suggestions") {
    this.runTest("jira-tests/als-719/test01.raml", Set("library01.raml"))
  }

  test("ALS-721 Multiple Library Suggestions 01") {
    this.runTest("jira-tests/als-721/test01.raml", Set("Employee", "Person", "Manager"))
  }

  test("ALS-721 Multiple Library Suggestions 02") {
    this.runTest("jira-tests/als-721/test02.raml", Set("Employee2", "Person2", "Manager2"))
  }

  test("ALS-722 check prefix in included directory") {
    this.runTest("jira-tests/als-722/api.raml", Set("dataType.raml"))
  }

  test("ALS-732 Single") {
    this.runTest("jira-tests/als-732/test01.raml", Set("trait1", "trait2"))
  }

  test("ALS-732 Sequence") {
    this.runTest("jira-tests/als-732/test02.raml", Set("trait2"))
  }

  test("ALS-732 Open Sequence") {
    this.runTest("jira-tests/als-732/test03.raml", Set("trait1", "trait2"))
  }

  test("ALS-732 Flow Sequence") {
    this.runTest("jira-tests/als-732/test04.raml", Set("trait2"))
  }

  //TODO: Check written values in non valid array
  ignore("ALS-732 Open Sequence with trait") {
    this.runTest("jira-tests/als-732/test04.raml", Set("trait2"))
  }

  test("Category matching test 01") {
    this.runTestCategory("jira-tests/category/test01.raml")
  }

  test("Category matching test 02") {
    this.runTestCategory("jira-tests/category/test02.raml")
  }

  test("Category matching test 03") {
    this.runTestCategory("jira-tests/category/test03.raml")
  }
}
