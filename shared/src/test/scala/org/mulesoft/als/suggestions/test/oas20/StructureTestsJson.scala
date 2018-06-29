package org.mulesoft.als.suggestions.test.oas20

class StructureTestsJson extends OAS20Test {

  test("SwaggerObject info"){
    this.runTest("structure_json/test01.json", Set("info"))
  }

  test("SwaggerObject definitions"){
    this.runTest("structure_json/test08.json", Set("definitions"))
  }

  test("SwaggerObject responses"){
    this.runTest("structure_json/test10.json", Set("responses"))
  }

  test("SwaggerObject securityDefinitions"){
    this.runTest("structure_json/test11.json", Set("securityDefinitions"))
  }

  test("SwaggerObject externalDocs"){
    this.runTest("structure_json/test14.json", Set("externalDocs"))
  }

  test("InfoObject version"){
    this.runTest("structure_json/test16.json", Set("version\":"))
  }

  test("InfoObject contact"){
    this.runTest("structure_json/test20.json", Set("contact"))
  }

  test("OperationObject summary"){
    this.runTest("structure_json/test34.json", Set("summary\":"))
  }

  test("OperationObject description"){
    this.runTest("structure_json/test35.json", Set("description"))
  }

  test("OperationObject externalDocs"){
    this.runTest("structure_json/test36.json", Set("externalDocs"))
  }

  test("OperationObject operationId"){
    this.runTest("structure_json/test37.json", Set("operationId"))
  }

  test("OperationObject responses"){
    this.runTest("structure_json/test41.json", Set("responses"))
  }

  test("OperationObject deprecated"){
    this.runTest("structure_json/test43.json", Set("deprecated"))
  }

  test("OperationObject security"){
    this.runTest("structure_json/test44.json", Set("security"))
  }

  test("ResponseObject description"){
    this.runTest("structure_json/test47.json", Set("description"))
  }

  test("ResponseObject schema"){
    this.runTest("structure_json/test48.json", Set("schema"))
  }

  test("ParameterObject name"){
    this.runTest("structure_json/test51.json", Set("name"))
  }

  test("BodyParameterObject schema"){
    this.runTest("structure_json/test56.json", Set("schema"))
  }

  test("ItemsObject format"){
    this.runTest("structure_json/test58.json", Set("format"))
  }

  test("ItemsObject exclusiveMaximum"){
    this.runTest("structure_json/test61.json", Set("exclusiveMaximum"))
  }

  test("ItemsObject minimum"){
    this.runTest("structure_json/test62.json", Set("minimum"))
  }

  test("ItemsObject exclusiveMinimum"){
    this.runTest("structure_json/test63.json", Set("exclusiveMinimum\":"))
  }

  test("ItemsObject maxLength"){
    this.runTest("structure_json/test64.json", Set("maxLength"))
  }

  test("ItemsObject pattern"){
    this.runTest("structure_json/test66.json", Set("pattern\":"))
  }

  test("ItemsObject maxItems"){
    this.runTest("structure_json/test67.json", Set("maxItems\":"))
  }

  test("ItemsObject minItems"){
    this.runTest("structure_json/test68.json", Set("minItems\":"))
  }

  test("ItemsObject example"){
    this.runTest("structure_json/test72.json", Set("example\":"))
  }

  test("ItemsObject items"){
    this.runTest("structure_json/test73.json", Set("items"))
  }

  test("ItemsObject collectionFormat"){
    this.runTest("structure_json/test74.json", Set("collectionFormat\":"))
  }

  test("SchemaObject title"){
    this.runTest("structure_json/test75.json", Set("title\":"))
  }

  test("SchemaObject description"){
    this.runTest("structure_json/test76.json", Set("description\":"))
  }

  test("SchemaObject properties"){
    this.runTest("structure_json/test77.json", Set("properties"))
  }

  test("SchemaObject discriminator"){
    this.runTest("structure_json/test78.json", Set("discriminator\":"))
  }

  test("SchemaObject readOnly"){
    this.runTest("structure_json/test85.json", Set("readOnly\":"))
  }

  test("TagObject description"){
    this.runTest("structure_json/test88.json", Set("description\":"))
  }

  test("TagObject externalDocs"){
    this.runTest("structure_json/test89.json", Set("externalDocs"))
  }

  test("XMLObject namespace"){
    this.runTest("structure_json/test104.json", Set("namespace\":"))
  }

  test("XMLObject prefix"){
    this.runTest("structure_json/test105.json", Set("prefix\":"))
  }
}