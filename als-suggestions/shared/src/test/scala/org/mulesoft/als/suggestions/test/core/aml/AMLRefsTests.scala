package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.core.CoreTest

class AMLRefsTests extends CoreTest {
  def rootPath: String = "AML/demo/otherDialects"

  def format: String = Aml.toString

  test("RamlStyle Flavour - suggests !include") {
    runTestForCustomDialect("styleInstance.yaml", "ramlStyleDialect.yaml", Set("!include"))
  }

  test("JsonStyle Flavour - no suggestion") {
    runTestForCustomDialect("styleInstance.yaml", "jsonStyleDialect.yaml", Set())
  }

  test("No Flavour specified - suggests !include") {
    runTestForCustomDialect("styleInstance.yaml", "noStyleDialect.yaml", Set("!include"))
  }

  test("RamlStyle Flavour in map - no suggestion") {
    runTestForCustomDialect("styleInstanceEOL.yaml", "ramlStyleDialect.yaml", Set())
  }

  test("JsonStyle Flavour in map - suggest #ref") {
    runTestForCustomDialect("styleInstanceEOL.yaml", "jsonStyleDialect.yaml", Set("long", "range", "name", "$ref"))
  }

  test("No Flavour specified in map (has facets) - does not suggest #ref") {
    runTestForCustomDialect("styleInstanceEOLWithFacets.yaml", "noStyleDialect.yaml", Set("long", "range"))
  }

  test("No Flavour specified in map - suggests #ref") {
    runTestForCustomDialect("styleInstanceEOL.yaml", "noStyleDialect.yaml", Set("$ref", "long", "range", "name"))
  }

  test("No Flavour specified in map - suggests #ref from Library") {
    runTestForCustomDialect("styleInstanceEOL.yaml", "noStyleDialectLib.yaml", Set("$ref", "long", "range", "name"))
  }

  test("No Flavour specified in map - suggests #ref from Fragment") {
    runTestForCustomDialect("styleInstanceEOL.yaml", "noStyleDialectFrag.yaml", Set("$ref", "long", "range", "name"))
  }
}
