package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.typesystem.definition.system.OasCommonMediaTypes

class StructureTestsOasYaml extends OAS20Test {

  test("SwaggerObject info") {
    this.runTest("structure/test01.yml", Set("info:\n  "))
  }

  test("SwaggerObject paths") {
    this.runTest("structure/test07.yml", Set("paths:\n  "))
  }

  test("SwaggerObject responses") {
    this.runTest("structure/test10.yml", Set("responses:\n  "))
  }

  test("InfoObject title") {
    this.runTest("structure/test15.yml", Set("title: "))
  }

  test("InfoObject version") {
    this.runTest("structure/test16.yml", Set("version: "))
  }

  test("InfoObject description") {
    this.runTest("structure/test17.yml", Set("description: "))
  }

  test("InfoObject contact") {
    this.runTest("structure/test20.yml", Set("contact:\n    "))
  }

  test("LicenseObject name") {
    this.runTest("structure/test21.yml", Set("name: "))
  }

  test("LicenseObject url") {
    this.runTest("structure/test22.yml", Set("url: "))
  }

  test("ContactObject name") {
    this.runTest("structure/test23.yml", Set("name: "))
  }

  test("ContactObject url") {
    this.runTest("structure/test24.yml", Set("url: "))
  }

  test("ContactObject email") {
    this.runTest("structure/test25.yml", Set("email: "))
  }

  test("ExternalDocumentationObject url") {
    this.runTest("structure/test26.yml", Set("url: "))
  }

  test("ExternalDocumentationObject description") {
    this.runTest("structure/test27.yml", Set("description: "))
  }

  test("PathItemObject post, put, patch") {
    this.runTest("structure/test29.yml", Set("post:\n      ", "put:\n      ", "patch:\n      "))
  }

  test("PathItemObject parameters") {
    this.runTest("structure/test33.yml", Set("parameters:\n      "))
  }

  test("OperationObject summary") {
    this.runTest("structure/test34.yml", Set("summary: "))
  }

  test("OperationObject description") {
    this.runTest("structure/test35.yml", Set("description: "))
  }

  test("OperationObject operationId") {
    this.runTest("structure/test37.yml", Set("operationId: "))
  }

  test("OperationObject parameters") {
    this.runTest("structure/test40.yml", Set("parameters:\n        "))
  }

  test("OperationObject responses") {
    this.runTest("structure/test41.yml", Set("responses:\n        "))
  }

  test("OperationObject deprecated") {
    this.runTest("structure/test43.yml", Set("deprecated: "))
  }

  test("OperationObject security") {
    this.runTest("structure/test44.yml", Set("security:\n        "))
  }

  test("ResponseObject description") {
    this.runTest("structure/test47.yml", Set("description: "))
  }

  test("ResponseObject schema") {
    this.runTest("structure/test48.yml", Set("schema:\n            "))
  }

  test("ResponseObject examples") {
    this.runTest("structure/test116.yml", Set("examples"))
  }

  test("ResponseObject headers") {
    this.runTest("structure/test117.yml", Set("headers"))
  }

  test("ParameterObject description") {
    this.runTest("structure/test52.yml", Set("description: "))
  }

  test("BodyParameterObject schema") {
    this.runTest("structure/test56.yml", Set("schema:\n          "))
  }

  test("ItemsObject format") {
    this.runTest("structure/test58.yml", Set("format: "))
  }

  test("ItemsObject default") {
    this.runTest("structure/test59.yml", Set("default: "))
  }

  test("ItemsObject maximum") {
    this.runTest("structure/test60.yml", Set("maximum: "))
  }

  test("ItemsObject minimum") {
    this.runTest("structure/test62.yml", Set("minimum: "))
  }

  test("ItemsObject pattern") {
    this.runTest("structure/test66.yml", Set("pattern: "))
  }

  test("ItemsObject multipleOf") {
    this.runTest("structure/test71.yml", Set("multipleOf: "))
  }

  test("ItemsObject example") {
    this.runTest("structure/test72.yml", Set("example: "))
  }

  test("ItemsObject items") {
    this.runTest("structure/test73.yml", Set("items:\n        "))
  }

  test("SchemaObject title") {
    this.runTest("structure/test75.yml", Set("title: "))
  }

  test("SchemaObject description") {
    this.runTest("structure/test76.yml", Set("description: "))
  }

  test("SchemaObject properties") {
    this.runTest("structure/test77.yml", Set("properties:\n      "))
  }

  test("SchemaObject discriminator") {
    this.runTest("structure/test78.yml", Set("discriminator: "))
  }

  test("SchemaObject xml") {
    this.runTest("structure/test79.yml", Set("xml:\n      "))
  }

  test("Parameter definition key") {
    this.runTest("structure/test106.yml", Set())
  }

  test("Response definition key") {
    this.runTest("structure/test107.yml", Set())
  }

  test("Definition required property") {
    this.runTest("structure/test108.yml", Set("name", "id"))
  }

  test("Security reference") {
    this.runTest("structure/test109.yml", Set("BasicAuth1: ", "BasicAuth2: "))
  }

  test("request parameter type 1") {
    this.runTest("structure/test110.yml", Set("query", "header", "path", "formData", "body"))
  }

  test("request parameter type 2") {
    this.runTest("structure/test111.yml", Set("query", "header", "path", "formData", "body"))
  }

  test("request parameter name 1") {
    this.runTest("structure/test112.yml", Set("queryParam1", "queryParam2"))
  }

  test("response codes test 01") {
    this.runTest("structure/test113.yml", TestOasResponseCodes.all.toSet)
  }

  test("response codes test 02") {
    this.runTest("structure/test114.yml", TestOasResponseCodes.all.toSet)
  }

  test("test property name suggestion") {
    this.runTest("structure/test115.yml", Set())
  }

  test("test root produces suggestions") {
    this.runTest("structure/produces/root.yml", OasCommonMediaTypes.all.toSet)
  }

  test("test root consumes suggestions") {
    this.runTest("structure/consumes/root.yml", OasCommonMediaTypes.all.toSet)
  }
  test("test operation produces suggestions") {
    this.runTest("structure/produces/operation.yml", OasCommonMediaTypes.all.toSet)
  }

  test("test operation consumes suggestions") {
    this.runTest("structure/consumes/operation.yml", OasCommonMediaTypes.all.toSet)
  }

  test("test schemes without line break") {
    this.runTest("structure/test128.yml", Set("schemes: "))
  }

  // todo: activate when start working with oas dialect
  ignore("test required properties names yaml") {
    this.runTest("structure/shapes/required-properties-name.yml", Set("name", "tag", "anotherPro"))
  }

  // todo: activate when start working with oas dialect
  ignore("test required properties names yaml existing") {
    this.runTest("structure/shapes/required-properties-name-existing.yml", Set("name", "anotherPro"))
  }
}
