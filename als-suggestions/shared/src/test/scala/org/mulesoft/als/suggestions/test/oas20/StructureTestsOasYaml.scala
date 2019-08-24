package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.typesystem.definition.system.{OasCommonMediaTypes, OasResponseCodes}

class StructureTestsOasYaml extends OAS20Test {

  test("SwaggerObject info") {
    this.runSuggestionTest("structure/test01.yml", Set("info:\n  "))
  }

  test("SwaggerObject paths") {
    this.runSuggestionTest("structure/test07.yml", Set("paths:\n  "))
  }

  test("SwaggerObject responses") {
    this.runSuggestionTest("structure/test10.yml", Set("responses:\n  "))
  }

  test("InfoObject title") {
    this.runSuggestionTest("structure/test15.yml", Set("title: "))
  }

  test("InfoObject version") {
    this.runSuggestionTest("structure/test16.yml", Set("version: "))
  }

  test("InfoObject description") {
    this.runSuggestionTest("structure/test17.yml", Set("description: "))
  }

  test("InfoObject contact") {
    this.runSuggestionTest("structure/test20.yml", Set("contact:\n    "))
  }

  test("LicenseObject name") {
    this.runSuggestionTest("structure/test21.yml", Set("name: "))
  }

  test("LicenseObject url") {
    this.runSuggestionTest("structure/test22.yml", Set("url: "))
  }

  test("ContactObject name") {
    this.runSuggestionTest("structure/test23.yml", Set("name: "))
  }

  test("ContactObject url") {
    this.runSuggestionTest("structure/test24.yml", Set("url: "))
  }

  test("ContactObject email") {
    this.runSuggestionTest("structure/test25.yml", Set("email: "))
  }

  test("ExternalDocumentationObject url") {
    this.runSuggestionTest("structure/test26.yml", Set("url: "))
  }

  test("ExternalDocumentationObject description") {
    this.runSuggestionTest("structure/test27.yml", Set("description: "))
  }

  test("PathItemObject post, put, patch") {
    this.runSuggestionTest("structure/test29.yml", Set("post:\n      ", "put:\n      ", "patch:\n      "))
  }

  test("PathItemObject parameters") {
    this.runSuggestionTest("structure/test33.yml", Set("parameters:\n      "))
  }

  test("OperationObject summary") {
    this.runSuggestionTest("structure/test34.yml", Set("summary: "))
  }

  test("OperationObject description") {
    this.runSuggestionTest("structure/test35.yml", Set("description: "))
  }

  test("OperationObject operationId") {
    this.runSuggestionTest("structure/test37.yml", Set("operationId: "))
  }

  test("OperationObject parameters") {
    this.runSuggestionTest("structure/test40.yml", Set("parameters:\n        "))
  }

  test("OperationObject responses") {
    this.runSuggestionTest("structure/test41.yml", Set("responses:\n        "))
  }

  test("OperationObject deprecated") {
    this.runSuggestionTest("structure/test43.yml", Set("deprecated: "))
  }

  test("OperationObject security") {
    this.runSuggestionTest("structure/test44.yml", Set("security:\n        "))
  }

  test("ResponseObject description") {
    this.runSuggestionTest("structure/test47.yml", Set("description: "))
  }

  test("ResponseObject schema") {
    this.runSuggestionTest("structure/test48.yml", Set("schema:\n            "))
  }

  test("ResponseObject examples") {
    this.runSuggestionTest("structure/test116.yml", Set("examples"))
  }

  test("ResponseObject headers") {
    this.runSuggestionTest("structure/test117.yml", Set("headers"))
  }

  test("ParameterObject description") {
    this.runSuggestionTest("structure/test52.yml", Set("description: "))
  }

  test("BodyParameterObject schema") {
    this.runSuggestionTest("structure/test56.yml", Set("schema:\n          "))
  }

  test("ItemsObject format") {
    this.runSuggestionTest("structure/test58.yml", Set("format: "))
  }

  test("ItemsObject default") {
    this.runSuggestionTest("structure/test59.yml", Set("default: "))
  }

  test("ItemsObject maximum") {
    this.runSuggestionTest("structure/test60.yml", Set("maximum: "))
  }

  test("ItemsObject minimum") {
    this.runSuggestionTest("structure/test62.yml", Set("minimum: "))
  }

  test("ItemsObject pattern") {
    this.runSuggestionTest("structure/test66.yml", Set("pattern: "))
  }

  test("ItemsObject multipleOf") {
    this.runSuggestionTest("structure/test71.yml", Set("multipleOf: "))
  }

  test("ItemsObject example") {
    this.runSuggestionTest("structure/test72.yml", Set("example: "))
  }

  test("ItemsObject items") {
    this.runSuggestionTest("structure/test73.yml", Set("items:\n        "))
  }

  test("SchemaObject title") {
    this.runSuggestionTest("structure/test75.yml", Set("title: "))
  }

  test("SchemaObject description") {
    this.runSuggestionTest("structure/test76.yml", Set("description: "))
  }

  test("SchemaObject properties") {
    this.runSuggestionTest("structure/test77.yml", Set("properties:\n      "))
  }

  test("SchemaObject discriminator") {
    this.runSuggestionTest("structure/test78.yml", Set("discriminator: "))
  }

  test("SchemaObject xml") {
    this.runSuggestionTest("structure/test79.yml", Set("xml:\n      "))
  }

  test("Parameter definition key") {
    this.runSuggestionTest("structure/test106.yml", Set())
  }

  ignore("Response definition key") {
    this.runSuggestionTest("structure/test107.yml", Set())
  }

  test("Definition required property") {
    this.runSuggestionTest("structure/test108.yml", Set("name", "id"))
  }

  test("Security reference") {
    this.runSuggestionTest("structure/test109.yml", Set("BasicAuth1:\n          ", "BasicAuth2:\n          "))
  }

  test("request parameter type 1") {
    this.runSuggestionTest("structure/test110.yml", Set("query", "header", "path", "formData", "body"))
  }

  test("request parameter type 2") {
    this.runSuggestionTest("structure/test111.yml", Set("query", "header", "path", "formData", "body"))
  }

  test("request parameter name 1") {
    this.runSuggestionTest("structure/test112.yml", Set("queryParam1", "queryParam2"))
  }

  test("response codes test 01") {
    this.runSuggestionTest("structure/test113.yml", TestOasResponseCodes.all.toSet)
  }

  test("response codes test 02") {
    this.runSuggestionTest("structure/test114.yml", TestOasResponseCodes.all.filter(r => !r.contains("200")).toSet)
  }

  test("test property name suggestion") {
    this.runSuggestionTest("structure/test115.yml", Set())
  }

  test("test root produces suggestions") {
    this.runSuggestionTest("structure/produces/root.yml", OasCommonMediaTypes.all.map(r => s"$r:\n    ").toSet)
  }

  test("test root consumes suggestions") {
    this.runSuggestionTest("structure/consumes/root.yml", OasCommonMediaTypes.all.map(r => s"$r:\n    ").toSet)
  }
  test("test operation produces suggestions") {
    this.runSuggestionTest("structure/produces/operation.yml",
                           OasCommonMediaTypes.all.map(r => s"$r:\n          ").toSet)
  }

  test("test operation consumes suggestions") {
    this.runSuggestionTest("structure/consumes/operation.yml",
                           OasCommonMediaTypes.all.map(r => s"$r:\n          ").toSet)
  }

  test("test schemes without line break") {
    this.runSuggestionTest("structure/test128.yml", Set("schemes: "))
  }

  // todo: activate when start working with oas dialect
  ignore("test required properties names yaml") {
    this.runSuggestionTest("structure/shapes/required-properties-name.yml", Set("name", "tag", "anotherPro"))
  }

  // todo: activate when start working with oas dialect
  ignore("test required properties names yaml existing") {
    this.runSuggestionTest("structure/shapes/required-properties-name-existing.yml", Set("name", "anotherPro"))
  }
}
