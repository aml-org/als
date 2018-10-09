package org.mulesoft.als.suggestions.test.oas20

class StructureTests extends OAS20Test {

  test("SwaggerObject info"){
    this.runTest("structure/test01.yml", Set("info:"))
  }

  test("SwaggerObject paths"){
    this.runTest("structure/test07.yml", Set("paths:"))
  }

  test("SwaggerObject responses"){
    this.runTest("structure/test10.yml", Set("responses:"))
  }

  test("InfoObject title"){
    this.runTest("structure/test15.yml", Set("title:"))
  }

  test("InfoObject version"){
    this.runTest("structure/test16.yml", Set("version:"))
  }

  test("InfoObject description"){
    this.runTest("structure/test17.yml", Set("description:"))
  }

  test("InfoObject contact"){
    this.runTest("structure/test20.yml", Set("contact:"))
  }

  test("LicenseObject name"){
    this.runTest("structure/test21.yml", Set("name:"))
  }

  test("LicenseObject url"){
    this.runTest("structure/test22.yml", Set("url:"))
  }

  test("ContactObject name"){
    this.runTest("structure/test23.yml", Set("name:"))
  }

  test("ContactObject url"){
    this.runTest("structure/test24.yml", Set("url:"))
  }

  test("ContactObject email"){
    this.runTest("structure/test25.yml", Set("email:"))
  }

  test("ExternalDocumentationObject url"){
    this.runTest("structure/test26.yml", Set("url:"))
  }

  test("ExternalDocumentationObject description"){
    this.runTest("structure/test27.yml", Set("description:"))
  }

  test("PathItemObject post, put, patch"){
    this.runTest("structure/test29.yml", Set("post:\n      ", "put:\n      ", "patch:\n      "))
  }

  test("PathItemObject parameters"){
    this.runTest("structure/test33.yml", Set("parameters:"))
  }

  test("OperationObject summary"){
    this.runTest("structure/test34.yml", Set("summary:"))
  }

  test("OperationObject description"){
    this.runTest("structure/test35.yml", Set("description:"))
  }

  test("OperationObject operationId"){
    this.runTest("structure/test37.yml", Set("operationId:"))
  }

  test("OperationObject parameters"){
    this.runTest("structure/test40.yml", Set("parameters:"))
  }

  test("OperationObject responses"){
    this.runTest("structure/test41.yml", Set("responses:"))
  }

  test("OperationObject deprecated"){
    this.runTest("structure/test43.yml", Set("deprecated:"))
  }

  test("OperationObject security"){
    this.runTest("structure/test44.yml", Set("security:"))
  }

  test("ResponseObject description"){
    this.runTest("structure/test47.yml", Set("description:"))
  }

  test("ResponseObject schema"){
    this.runTest("structure/test48.yml", Set("schema:"))
  }

  test("ParameterObject description"){
    this.runTest("structure/test52.yml", Set("description:"))
  }

  test("BodyParameterObject schema"){
    this.runTest("structure/test56.yml", Set("schema:"))
  }

  test("ItemsObject format"){
    this.runTest("structure/test58.yml", Set("format:"))
  }

  test("ItemsObject default"){
    this.runTest("structure/test59.yml", Set("default:"))
  }

  test("ItemsObject maximum"){
    this.runTest("structure/test60.yml", Set("maximum:"))
  }

  test("ItemsObject minimum"){
    this.runTest("structure/test62.yml", Set("minimum:"))
  }

  test("ItemsObject pattern"){
    this.runTest("structure/test66.yml", Set("pattern:"))
  }

  test("ItemsObject multipleOf"){
    this.runTest("structure/test71.yml", Set("multipleOf:"))
  }

  test("ItemsObject example"){
    this.runTest("structure/test72.yml", Set("example:"))
  }

  test("ItemsObject items"){
    this.runTest("structure/test73.yml", Set("items:"))
  }

  test("SchemaObject title"){
    this.runTest("structure/test75.yml", Set("title:"))
  }

  test("SchemaObject description"){
    this.runTest("structure/test76.yml", Set("description:"))
  }

  test("SchemaObject properties"){
    this.runTest("structure/test77.yml", Set("properties:"))
  }

  test("SchemaObject discriminator"){
    this.runTest("structure/test78.yml", Set("discriminator:"))
  }

  test("SchemaObject xml"){
    this.runTest("structure/test79.yml", Set("xml:"))
  }

  test("Parameter definition key"){
    this.runTest("structure/test106.yml", Set())
  }

  test("Response definition key"){
    this.runTest("structure/test107.yml", Set())
  }
}
