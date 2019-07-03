package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.core.CoreTest

class BasicCoreTestsAML extends CoreTest {

  def rootPath: String = "AML/demo"

  def format: String = Aml.toString

  test("full root structure") {
    this.runTestForCustomDialect("visit01.yaml", "dialect.yaml", Set("office", "date", "meetings"))
  }

  test("some root structure") {
    this.runTestForCustomDialect("visit02.yaml", "dialect.yaml", Set("date", "meetings"))
  }

  test("root structure with prefix") {
    this.runTestForCustomDialect("visit03.yaml", "dialect.yaml", Set("office"))
  }
}
