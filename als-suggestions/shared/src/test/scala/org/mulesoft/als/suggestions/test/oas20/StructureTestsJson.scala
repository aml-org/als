package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.typesystem.definition.system.{OasCommonMediaTypes, OasResponseCodes}

class StructureTestsJson extends OAS20Test {

  test("SwaggerObject info") {
    this.runSuggestionTest("structure_json/test01.json", Set("info"))
  }

  test("SwaggerObject definitions") {
    this.runSuggestionTest("structure_json/test08.json", Set("definitions"))
  }

  test("SwaggerObject responses") {
    this.runSuggestionTest("structure_json/test10.json", Set("responses"))
  }

  test("SwaggerObject securityDefinitions") {
    this.runSuggestionTest("structure_json/test11.json", Set("securityDefinitions"))
  }

  test("SwaggerObject externalDocs") {
    this.runSuggestionTest("structure_json/test14.json", Set("externalDocs"))
  }

  test("InfoObject version") {
    this.runSuggestionTest("structure_json/test16.json", Set("version"))
  }

  test("InfoObject contact") {
    this.runSuggestionTest("structure_json/test20.json", Set("contact"))
  }

  test("OperationObject summary") {
    this.runSuggestionTest("structure_json/test34.json", Set("summary"))
  }

  test("OperationObject description") {
    this.runSuggestionTest("structure_json/test35.json", Set("description"))
  }

  test("OperationObject externalDocs") {
    this.runSuggestionTest("structure_json/test36.json", Set("externalDocs"))
  }

  test("OperationObject operationId") {
    this.runSuggestionTest("structure_json/test37.json", Set("operationId"))
  }

  test("OperationObject parameters") {
    this.runSuggestionTest("structure_json/test40.json", Set("parameters"))
  }

  test("OperationObject responses") {
    this.runSuggestionTest("structure_json/test41.json", Set("responses"))
  }

  test("OperationObject deprecated") {
    this.runSuggestionTest("structure_json/test43.json", Set("deprecated"))
  }

  test("OperationObject security") {
    this.runSuggestionTest("structure_json/test44.json", Set("security"))
  }

  test("ResponseObject description") {
    this.runSuggestionTest("structure_json/test47.json", Set("description"))
  }

  test("ResponseObject schema") {
    this.runSuggestionTest("structure_json/test48.json", Set("schema"))
  }

  test("ResponseObject examples") {
    this.runSuggestionTest("structure_json/test111.json", Set("examples"))
  }

  test("ResponseObject headers") {
    this.runSuggestionTest("structure_json/test112.json", Set("headers"))
  }

  test("ParameterObject name") {
    this.runSuggestionTest("structure_json/test51.json", Set("name"))
  }

  test("BodyParameterObject schema") {
    this.runSuggestionTest("structure_json/test56.json", Set("schema"))
  }

  test("ItemsObject format") {
    this.runSuggestionTest("structure_json/test58.json", Set("format"))
  }

  test("ItemsObject exclusiveMaximum") {
    this.runSuggestionTest("structure_json/test61.json", Set("exclusiveMaximum"))
  }

  test("ItemsObject minimum") {
    this.runSuggestionTest("structure_json/test62.json", Set("minimum"))
  }

  test("ItemsObject exclusiveMinimum") {
    this.runSuggestionTest("structure_json/test63.json", Set("exclusiveMinimum"))
  }

  test("ItemsObject maxLength") {
    this.runSuggestionTest("structure_json/test64.json", Set("maxLength"))
  }

  test("ItemsObject pattern") {
    this.runSuggestionTest("structure_json/test66.json", Set("pattern"))
  }

  test("ItemsObject maxItems") {
    this.runSuggestionTest("structure_json/test67.json", Set("maxItems"))
  }

  test("ItemsObject minItems") {
    this.runSuggestionTest("structure_json/test68.json", Set("minItems"))
  }

  test("ItemsObject example") {
    this.runSuggestionTest("structure_json/test72.json", Set("example"))
  }

  test("ItemsObject items") {
    this.runSuggestionTest("structure_json/test73.json", Set("items"))
  }

  test("ItemsObject collectionFormat") {
    this.runSuggestionTest("structure_json/test74.json", Set("collectionFormat"))
  }

  test("SchemaObject title") {
    this.runSuggestionTest("structure_json/test75.json", Set("title"))
  }

  test("SchemaObject description") {
    this.runSuggestionTest("structure_json/test76.json", Set("description"))
  }

  test("SchemaObject properties") {
    this.runSuggestionTest("structure_json/test77.json", Set("properties"))
  }

  test("SchemaObject discriminator") {
    this.runSuggestionTest("structure_json/test78.json", Set("discriminator"))
  }

  test("SchemaObject readOnly") {
    this.runSuggestionTest("structure_json/test85.json", Set("readOnly\":"))
  }

  test("TagObject description") {
    this.runSuggestionTest("structure_json/test88.json", Set("description\":"))
  }

  test("TagObject externalDocs") {
    this.runSuggestionTest("structure_json/test89.json", Set("externalDocs"))
  }

  test("XMLObject namespace") {
    this.runSuggestionTest("structure_json/test104.json", Set("namespace\":"))
  }

  test("XMLObject prefix") {
    this.runSuggestionTest("structure_json/test105.json", Set("prefix\":"))
  }

  test("complete key start (element property)") {
    this.runSuggestionTest(
      "structure_json/test106.json",
      Set("responses",
          "securityDefinitions",
          "parameters",
          "definitions",
          "security",
          "consumes",
          "externalDocs",
          "host",
          "schemes",
          "produces",
          "basePath")
    )
  }

  test("incomplete key end") {
    this.runSuggestionTest("structure_json/test107.json", Set("parameters\":", "paths\":"))
  }

  test("empty open quote") {
    this.runSuggestionTest(
      "structure_json/test108.json",
      Set(
        "paths\":",
        "responses\":",
        "securityDefinitions\":",
        "parameters\":",
        "definitions\":",
        "security\":",
        "consumes\":",
        "externalDocs\":",
        "host\":",
        "schemes\":",
        "produces\":",
        "basePath\":"
      )
    )
  }

  test("response codes test 01") {
    this.runSuggestionTest("structure_json/test109.json", OasResponseCodes.all.toSet)
  }

  test("response codes test 02") {
    this.runSuggestionTest("structure_json/test110.json", OasResponseCodes.all.toSet)
  }

  test("test root produces suggestions") {
    this.runSuggestionTest("structure_json/produces/root.json", OasCommonMediaTypes.all.toSet)
  }

  test("test root consumes suggestions") {
    this.runSuggestionTest("structure_json/consumes/root.json", OasCommonMediaTypes.all.toSet)
  }
  test("test operation produces suggestions") {
    this.runSuggestionTest("structure_json/produces/operation.json", OasCommonMediaTypes.all.toSet)
  }

  test("test operation consumes suggestions") {
    this.runSuggestionTest("structure_json/consumes/operation.json", OasCommonMediaTypes.all.toSet)
  }

  // todo: activate when start working with oas dialect
  ignore("test required properties names") {
    this.runSuggestionTest("structure_json/shapes/required-properties-name.json", Set("name", "tag", "anotherPro"))
  }
}
