package org.mulesoft.als.suggestions.test.aml

import amf.ProfileName

class ReferenceTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/references"

  ignore("RamlStyle Flavour - suggests !include") {
    withDialect("instances/ramlStyleInstance.yaml",
                Set("!include "),
                "dialects/ramlStyleDialect.yaml",
                ProfileName("RAMLStyle 1.0"))
  }

  test("JsonSchemaStyle Flavour - no suggestion") {
    withDialect("instances/jsonStyleInstance.yaml",
                Set(),
                "dialects/jsonStyleDialect.yaml",
                ProfileName("JSONStyle 1.0"))
  }

  ignore("No Flavour specified - suggests !include") {
    withDialect("instances/noStyleInstance.yaml",
                Set("!include "),
                "dialects/noStyleDialect.yaml",
                ProfileName("NOStyle 1.0"))
  }

  test("RamlStyle Flavour in map - no suggestion") {
    withDialect("instances/ramlStyleInstanceEOL.yaml",
                Set(),
                "dialects/ramlStyleDialect.yaml",
                ProfileName("RAMLStyle 1.0"))
  }

  test("JsonSchemaStyle Flavour in map - suggest #ref") {
    withDialect("instances/jsonStyleInstanceEOL.yaml",
                Set("long: ", "range: ", "name: ", "$ref: "),
                "dialects/jsonStyleDialect.yaml",
                ProfileName("JSONStyle"))
  }

  test("No Flavour specified in map (has facets) - does not suggest #ref") {
    withDialect("instances/styleInstanceEOLWithFacets.yaml",
                Set("long: ", "range: "),
                "dialects/noStyleDialect.yaml",
                ProfileName("NOStyle 1.0"))
  }

  test("No Flavour specified in map - suggests #ref") {
    withDialect("instances/noStyleInstanceEOL.yaml",
                Set("$ref: ", "long: ", "range: ", "name: "),
                "dialects/noStyleDialect.yaml",
                ProfileName("NOStyle 1.0"))
  }

  test("No Flavour specified in map - suggests #ref from Library") {
    withDialect("instances/noStyleInstanceEOLLib.yaml",
                Set("$ref: ", "long: ", "range: ", "name: "),
                "dialects/noStyleDialectLib.yaml",
                ProfileName("NOStyleLib 1.0"))
  }

  test("No Flavour specified in map - suggests #ref from Fragment") {
    withDialect("instances/noStyleInstanceEOLFrag.yaml",
                Set("$ref: ", "long: ", "range: ", "name: "),
                "dialects/noStyleDialectFrag.yaml",
                ProfileName("NOStyleFrag 1.0"))
  }

  test("RamlStyle path suggestion - !include tag value") {
    withDialect(
      "instances/ramlStylePathInstanceInclude.yaml",
      Set("test file 1.yaml", "test file 2.raml", "test dir/", "inner dir/"),
      "dialects/ramlStyleDialect.yaml",
      ProfileName("RAMLStyle 1.0")
    )
  }

  test("No Style path suggestion - !include tag value") {
    withDialect(
      "instances/noStylePathInstanceInclude.yaml",
      Set("test file 1.yaml", "test file 2.raml", "test dir/", "inner dir/"),
      "dialects/noStyleDialect.yaml",
      ProfileName("NOStyle 1.0")
    )
  }

  test("JsonSchemaStyle path suggestion - !include tag value") {
    withDialect("instances/jsonStylePathInstanceInclude.yaml",
                Set(),
                "dialects/jsonStyleDialect.yaml",
                ProfileName("JSONStyle 1.0"))
  }

  test("RamlStyle path suggestion - $ref tag value") {
    withDialect("instances/ramlStylePathInstanceRef.yaml",
                Set(),
                "dialects/ramlStyleDialect.yaml",
                ProfileName("RAMLStyle 1.0"))
  }

  test("No Style path suggestion - $ref tag value") {
    withDialect(
      "instances/noStylePathInstanceRef.yaml",
      Set("test file 1.yaml", "test file 2.raml", "test dir/", "inner dir/"),
      "dialects/noStyleDialect.yaml",
      ProfileName("NOStyle 1.0")
    )
  }

  test("JsonSchemaStyle path suggestion - $ref tag value") {
    withDialect(
      "instances/jsonStylePathInstanceRef.yaml",
      Set("test file 1.yaml", "test file 2.raml", "test dir/"),
      "dialects/jsonStyleDialect.yaml",
      ProfileName("JSONStyle 1.0")
    )
  }

  test("RamlStyle absolute path suggestion - starting with slash only") {
    withDialect(
      "instances/absolute-to-root/raml-style-absolute-empty-path.yaml",
      Set("/reference.yml", "/raml-style-empty-path.yaml"),
      "dialects/ramlStyleDialect.yaml",
      ProfileName("RAMLStyle 1.0")
    )
  }

  test("RamlStyle relative empty path suggestion - without slash") {
    withDialect(
      "instances/absolute-to-root/raml-style-empty-path.yaml",
      Set("reference.yml", "raml-style-absolute-empty-path.yaml"),
      "dialects/ramlStyleDialect.yaml",
      ProfileName("RAMLStyle 1.0")
    )
  }
}
