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

}
