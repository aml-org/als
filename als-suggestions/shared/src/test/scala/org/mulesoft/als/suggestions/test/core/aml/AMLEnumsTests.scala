package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.core.CoreTest

class AMLEnumsTests extends CoreTest {
  def rootPath: String = "AML/demo/otherDialects"

  def format: String = Aml.toString

  test("AllowMultiple Enum - Single Value") {
    runTestForCustomDialect("testArraysInstance02.yaml",
                            "testArraysDialect.yaml",
                            Set("First", "Second", "Third", "Fourth"))
  }
  test("AllowMultiple Enum - Multiple Value") {
    runTestForCustomDialect("testArraysInstance01.yaml", "testArraysDialect.yaml", Set("Second", "Third", "Fourth"))
  }
  test("AllowMultiple Enum - Single Value w/prefix") {
    runTestForCustomDialect("testArraysInstance03.yaml", "testArraysDialect.yaml", Set("First", "Fourth"))
  }
  test("AllowMultiple Enum - Multiple Value w/prefix") {
    runTestForCustomDialect("testArraysInstance04.yaml", "testArraysDialect.yaml", Set("Fourth"))
  }
}
