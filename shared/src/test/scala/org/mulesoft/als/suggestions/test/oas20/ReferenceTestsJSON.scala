package org.mulesoft.als.suggestions.test.oas20

class ReferenceTestsJSON extends OAS20Test {

  test("Shema reference test JSON 001"){
    this.runTest("references_json/schemas/test001.json", Set("{ \"$ref\": \"#/definitions/Type1\" }", "{ \"$ref\": \"#/definitions/Type2\" }"))
  }

  test("Shema reference test JSON 002"){
    this.runTest("references_json/schemas/test002.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 003"){
    this.runTest("references_json/schemas/test003.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 004"){
    this.runTest("references_json/schemas/test004.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 005"){
      this.runTest("references_json/schemas/test005.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 006"){
    this.runTest("references_json/schemas/test006.json", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 007"){
    this.runTest("references_json/schemas/test007.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 008"){
    this.runTest("references_json/schemas/test008.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 009"){
    this.runTest("references_json/schemas/test009.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 010"){
    this.runTest("references_json/schemas/test010.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 011"){
    this.runTest("references_json/schemas/test011.json", Set("{ \"$ref\": \"#/definitions/Type1\" }", "{ \"$ref\": \"#/definitions/Type2\" }"))
  }

  test("Shema reference test JSON 012"){
    this.runTest("references_json/schemas/test012.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 013"){
    this.runTest("references_json/schemas/test013.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 014"){
    this.runTest("references_json/schemas/test014.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 015"){
      this.runTest("references_json/schemas/test015.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 016"){
    this.runTest("references_json/schemas/test016.json", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 017"){
    this.runTest("references_json/schemas/test017.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 018"){
    this.runTest("references_json/schemas/test018.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 019"){
    this.runTest("references_json/schemas/test019.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 020"){
    this.runTest("references_json/schemas/test020.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 021"){
    this.runTest("references_json/schemas/test021.json", Set("{ \"$ref\": \"#/definitions/Type1\" }", "{ \"$ref\": \"#/definitions/Type2\" }"))
  }

  test("Shema reference test JSON 022"){
    this.runTest("references_json/schemas/test022.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 023"){
    this.runTest("references_json/schemas/test023.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 024"){
    this.runTest("references_json/schemas/test024.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 025"){
      this.runTest("references_json/schemas/test025.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 026"){
    this.runTest("references_json/schemas/test026.json", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 027"){
    this.runTest("references_json/schemas/test027.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 028"){
    this.runTest("references_json/schemas/test028.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 029"){
    this.runTest("references_json/schemas/test029.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 030"){
    this.runTest("references_json/schemas/test030.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 031"){
    this.runTest("references_json/schemas/test031.json", Set("{ \"$ref\": \"#/definitions/Type1\" }", "{ \"$ref\": \"#/definitions/Type2\" }"))
  }

  test("Shema reference test JSON 032"){
    this.runTest("references_json/schemas/test032.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 033"){
    this.runTest("references_json/schemas/test033.json", Set("\"$ref\": \"#/definitions/Type1\"", "\"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 034"){
    this.runTest("references_json/schemas/test034.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 035"){
      this.runTest("references_json/schemas/test035.json", Set("$ref\": \"#/definitions/Type1", "$ref\": \"#/definitions/Type2", "maxItems\":", "exclusiveMinimum\":", "required\":", "collectionFormat\":", "enum\":", "items\":", "exclusiveMaximum\":", "type\":", "additionalProperties\":", "xml\":", "minimum\":", "discriminator\":", "maximum\":", "default\":", "pattern\":", "multipleOf\":", "description\":", "readOnly\":", "maxLength\":", "properties\":", "title\":", "minLength\":", "minItems\":", "$ref\":", "example\":", "format\":", "uniqueItems\":"))
  }

  test("Shema reference test JSON 036"){
    this.runTest("references_json/schemas/test036.json", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test JSON 037"){
    this.runTest("references_json/schemas/test037.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 038"){
    this.runTest("references_json/schemas/test038.json", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test JSON 039"){
    this.runTest("references_json/schemas/test039.json", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test JSON 040"){
    this.runTest("references_json/schemas/test040.json", Set("/definitions/Type1", "/definitions/Type2"))
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

 test("Response reference test JSON 001"){
    this.runTest("references_json/responses/test001.json", Set("{ \"$ref\": \"#/responses/response1\" }", "{ \"$ref\": \"#/responses/response2\" }"))
  }

  test("Response reference test JSON 002"){
    this.runTest("references_json/responses/test002.json", Set("\"$ref\": \"#/responses/response1\"", "\"$ref\": \"#/responses/response2\""))
  }

  test("Response reference test JSON 003"){
    this.runTest("references_json/responses/test003.json", Set("$ref\": \"#/responses/response1", "$ref\": \"#/responses/response2", "description\":", "$ref\":", "schema\":", "example\":"))
  }

  test("Response reference test JSON 004"){
    this.runTest("references_json/responses/test004.json", Set("\"$ref\": \"#/responses/response1\"", "\"$ref\": \"#/responses/response2\""))
  }

  test("Response reference test JSON 005"){
    this.runTest("references_json/responses/test005.json", Set("$ref\": \"#/responses/response1", "$ref\": \"#/responses/response2", "description\":", "$ref\":", "schema\":", "example\":"))
  }

  test("Response reference test JSON 006"){
    this.runTest("references_json/responses/test006.json", Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test JSON 007"){
    this.runTest("references_json/responses/test007.json", Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test JSON 008"){
    this.runTest("references_json/responses/test008.json", Set("/responses/response1", "/responses/response2"))
  }

  test("Response reference test JSON 009"){
    this.runTest("references_json/responses/test009.json", Set("/responses/response1", "/responses/response2"))
  }

  test("Response reference test JSON 010"){
    this.runTest("references_json/responses/test010.json", Set("#/responses/response1", "#/responses/response2"))
  }
}
