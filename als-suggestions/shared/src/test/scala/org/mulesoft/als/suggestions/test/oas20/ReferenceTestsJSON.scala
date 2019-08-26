package org.mulesoft.als.suggestions.test.oas20

class ReferenceTestsJSON extends OAS20Test {

  ignore("Schema reference test JSON 001") {
    this.runSuggestionTest("references_json/schemas/test001.json", Set.empty)
  }

  ignore("Schema reference test JSON 002") {
    this.runSuggestionTest("references_json/schemas/test002.json",
                           Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  ignore("Schema reference test JSON 003") {
    this.runSuggestionTest(
      "references_json/schemas/test003.json",
      Set(
        "\"enum\":",
        "\"properties\":",
        "\"collectionFormat\":",
        "\"default\":",
        "\"discriminator\":",
        "\"uniqueItems\":",
        "\"format\":",
        "\"description\":",
        "\"multipleOf\":",
        "\"readOnly\":",
        "\"title\":",
        "\"minimum\":",
        "\"exclusiveMinimum\":",
        "\"$ref\":",
        "\"items\":",
        "\"maxItems\":",
        "\"pattern\":",
        "\"example\":",
        "\"maxLength\":",
        "\"xml\":",
        "\"minLength\":",
        "\"exclusiveMaximum\":",
        "\"maximum\":",
        "\"minItems\":",
        "\"required\":",
        "\"additionalProperties\":",
        "\"type\":"
      )
    )
  }

  ignore("Schema reference test JSON 004") {
    this.runSuggestionTest(
      "references_json/schemas/test004.json",
      Set(
        "maxItems",
        "exclusiveMinimum",
        "required",
        "collectionFormat",
        "enum",
        "items",
        "exclusiveMaximum",
        "type",
        "additionalProperties",
        "xml",
        "minimum",
        "discriminator",
        "maximum",
        "default",
        "pattern",
        "multipleOf",
        "description",
        "readOnly",
        "maxLength",
        "properties",
        "title",
        "minLength",
        "minItems",
        "$ref",
        "example",
        "format",
        "uniqueItems"
      )
    )
  }

  ignore("Schema reference test JSON 005") {
    this.runSuggestionTest(
      "references_json/schemas/test005.json",
      Set(
        "maxItems",
        "exclusiveMinimum",
        "required",
        "collectionFormat",
        "enum",
        "items",
        "exclusiveMaximum",
        "type",
        "additionalProperties",
        "xml",
        "minimum",
        "discriminator",
        "maximum",
        "default",
        "pattern",
        "multipleOf",
        "description",
        "readOnly",
        "maxLength",
        "properties",
        "title",
        "minLength",
        "minItems",
        "$ref",
        "example",
        "format",
        "uniqueItems"
      )
    )
  }

  ignore("Schema reference test JSON 006") {
    this.runSuggestionTest("references_json/schemas/test006.json",
                           Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  ignore("Schema reference test JSON 007") {
    this.runSuggestionTest("references_json/schemas/test007.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  ignore("Schema reference test JSON 008") {
    this.runSuggestionTest("references_json/schemas/test008.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 009") {
    this.runSuggestionTest("references_json/schemas/test009.json", Set("definitions/Type1", "definitions/Type2"))
  }

  test("Schema reference test JSON 010") {
    this.runSuggestionTest("references_json/schemas/test010.json", Set("definitions/Type1", "definitions/Type2"))
  }

  //todo: ref to type 2 is incorrect
  ignore("Schema reference test JSON 011") {
    this.runSuggestionTest("references_json/schemas/test011.json", Set.empty)
  }

  ignore("Schema reference test JSON 012") {
    this.runSuggestionTest("references_json/schemas/test012.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  ignore("Schema reference test JSON 016") {
    this.runSuggestionTest("references_json/schemas/test016.json",
                           Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  ignore("Schema reference test JSON 017") {
    this.runSuggestionTest("references_json/schemas/test017.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 018") {
    this.runSuggestionTest("references_json/schemas/test018.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 019") {
    this.runSuggestionTest("references_json/schemas/test019.json", Set("definitions/Type1", "definitions/Type2"))
  }

  test("Schema reference test JSON 020") {
    this.runSuggestionTest("references_json/schemas/test020.json", Set("definitions/Type1", "definitions/Type2"))
  }

  ignore("Schema reference test JSON 026") {
    this.runSuggestionTest("references_json/schemas/test026.json",
                           Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  ignore("Schema reference test JSON 027") {
    this.runSuggestionTest("references_json/schemas/test027.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 028") {
    this.runSuggestionTest("references_json/schemas/test028.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 029") {
    this.runSuggestionTest("references_json/schemas/test029.json", Set("definitions/Type1", "definitions/Type2"))
  }

  test("Schema reference test JSON 030") {
    this.runSuggestionTest("references_json/schemas/test030.json", Set("definitions/Type1", "definitions/Type2"))
  }

  test("Schema reference test JSON 031") {
    this.runSuggestionTest("references_json/schemas/test031.json", Set.empty)
  }

  // todo: fix json patcher
  ignore("Schema reference test JSON 033") {
    this.runSuggestionTest(
      "references_json/schemas/test033.json",
      Set(
        "\"enum\":",
        "\"properties\":",
        "\"collectionFormat\":",
        "\"default\":",
        "\"discriminator\":",
        "\"uniqueItems\":",
        "\"format\":",
        "\"description\":",
        "\"multipleOf\":",
        "\"readOnly\":",
        "\"title\":",
        "\"minimum\":",
        "\"exclusiveMinimum\":",
        "\"$ref\":",
        "\"items\":",
        "\"maxItems\":",
        "\"pattern\":",
        "\"example\":",
        "\"maxLength\":",
        "\"xml\":",
        "\"minLength\":",
        "\"exclusiveMaximum\":",
        "\"maximum\":",
        "\"minItems\":",
        "\"required\":",
        "\"additionalProperties\":",
        "\"type\":"
      )
    )
  }

  ignore("Schema reference test JSON 036") {
    this.runSuggestionTest("references_json/schemas/test036.json",
                           Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  ignore("Schema reference test JSON 037") {
    this.runSuggestionTest("references_json/schemas/test037.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 038") {
    this.runSuggestionTest("references_json/schemas/test038.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Schema reference test JSON 039") {
    this.runSuggestionTest("references_json/schemas/test039.json", Set("definitions/Type1", "definitions/Type2"))
  }

  test("Schema reference test JSON 040") {
    this.runSuggestionTest("references_json/schemas/test040.json", Set("definitions/Type1", "definitions/Type2"))
  }

//  test("Parameter reference test JSON 001"){
//    this.runTest("references_json/parameters/test001.json", Set("\"$ref\": \"#/parameters/p1\"", "\"$ref\": \"#/parameters/p2\""))
//  }
//
//  test("Parameter reference test JSON 002"){
//    this.runTest("references_json/parameters/test002.json", Set("$ref\": \"#/parameters/p1", "$ref\": \"#/parameters/p2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "minimum\":", "default\":", "maximum\":", "pattern\":", "multipleOf\":", "description\":", "allowEmptyValue\":", "name\":", "in\":", "maxLength\":", "minLength\":", "minItems\":", "$ref\":", "format\":", "example\":", "uniqueItems\":"))
//  }
//
//  test("Parameter reference test JSON 003"){
//    this.runTest("references_json/parameters/test003.json", Set("\"$ref\": \"#/parameters/p1\"", "\"$ref\": \"#/parameters/p2\""))
//  }
//
//  test("Parameter reference test JSON 004"){
//    this.runTest("references_json/parameters/test004.json", Set("$ref\": \"#/parameters/p1", "$ref\": \"#/parameters/p2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "minimum\":", "default\":", "maximum\":", "pattern\":", "multipleOf\":", "description\":", "allowEmptyValue\":", "name\":", "in\":", "maxLength\":", "minLength\":", "minItems\":", "$ref\":", "format\":", "example\":", "uniqueItems\":"))
//  }

//  test("Parameter reference test JSON 005"){
//    this.runTest("references_json/parameters/test005.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 006"){
//    this.runTest("references_json/parameters/test006.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 007"){
//    this.runTest("references_json/parameters/test007.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 008"){
//    this.runTest("references_json/parameters/test008.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 009"){
//    this.runTest("references_json/parameters/test009.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }

//  test("Parameter reference test JSON 010"){
//    this.runTest("references_json/parameters/test010.json", Set("\"$ref\": \"#/parameters/p1\"", "\"$ref\": \"#/parameters/p2\""))
//  }
//
//  test("Parameter reference test JSON 011"){
//    this.runTest("references_json/parameters/test011.json", Set("$ref\": \"#/parameters/p1", "$ref\": \"#/parameters/p2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "minimum\":", "default\":", "maximum\":", "pattern\":", "multipleOf\":", "description\":", "allowEmptyValue\":", "name\":", "in\":", "maxLength\":", "minLength\":", "minItems\":", "$ref\":", "format\":", "example\":", "uniqueItems\":"))
//  }
//
//  test("Parameter reference test JSON 012"){
//    this.runTest("references_json/parameters/test012.json", Set("\"$ref\": \"#/parameters/p1\"", "\"$ref\": \"#/parameters/p2\""))
//  }
//
//  test("Parameter reference test JSON 013"){
//    this.runTest("references_json/parameters/test013.json", Set("$ref\": \"#/parameters/p1", "$ref\": \"#/parameters/p2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "minimum\":", "default\":", "maximum\":", "pattern\":", "multipleOf\":", "description\":", "allowEmptyValue\":", "name\":", "in\":", "maxLength\":", "minLength\":", "minItems\":", "$ref\":", "format\":", "example\":", "uniqueItems\":"))
//  }

//  test("Parameter reference test JSON 014"){
//    this.runTest("references_json/parameters/test014.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 015"){
//    this.runTest("references_json/parameters/test015.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 016"){
//    this.runTest("references_json/parameters/test016.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 017"){
//    this.runTest("references_json/parameters/test017.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }
//
//  test("Parameter reference test JSON 018"){
//    this.runTest("references_json/parameters/test018.json", Set("#/parameters/p1", "\"#/parameters/p2"))
//  }

  ignore("Response reference test JSON 001") {
    this.runSuggestionTest("references_json/responses/test001.json", Set.empty)
  }

  ignore("Response reference test JSON 002") {
    this.runSuggestionTest("references_json/responses/test002.json",
                           Set("\"description\":", "\"$ref\":", "\"headers\":", "\"schema\":", "\"examples\":"))
  }

  ignore("Response reference test JSON 003") {
    this.runSuggestionTest(
      "references_json/responses/test003.json",
      Set("#/responses/response1", "#/responses/response2")
    )
  }

  ignore("Response reference test JSON 006") {
    this.runSuggestionTest("references_json/responses/test006.json",
                           Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test JSON 007") {
    this.runSuggestionTest("references_json/responses/test007.json",
                           Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test JSON 008") {
    this.runSuggestionTest("references_json/responses/test008.json", Set("responses/response1", "responses/response2"))
  }

  test("Response reference test JSON 009") {
    this.runSuggestionTest("references_json/responses/test009.json", Set("responses/response1", "responses/response2"))
  }

  ignore("Response reference test JSON 010") {
    this.runSuggestionTest("references_json/responses/test010.json",
                           Set("#/responses/response1", "#/responses/response2"))
  }
}
