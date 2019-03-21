package org.mulesoft.language.outline.test.raml10.structure

import org.mulesoft.language.outline.test.raml10.RAML10StructureTest

class JiraStructureTests extends RAML10StructureTest {

  override def rootPath: String = "RAML10/jira-tests"

  test("ALS-741 - Complex structure") {
    this.runTest("als-741/test01.raml", "als-741/test01-outline.json")
  }
}
