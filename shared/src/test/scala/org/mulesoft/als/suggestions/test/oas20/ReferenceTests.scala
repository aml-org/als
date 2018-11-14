package org.mulesoft.als.suggestions.test.oas20

class ReferenceTests extends OAS20Test {

  test("Shema reference test 001"){
    this.runTest("references/schemas/test001.yml", Set("\n          \"$ref\": \"#/definitions/Type1\"", "\n          \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 002"){
    this.runTest("references/schemas/test002.yml", Set("\n          \"$ref\": \"#/definitions/Type1\"", "\n          \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 003"){
    this.runTest("references/schemas/test003.yml", Set(" \"#/definitions/Type1\"", " \"#/definitions/Type2\""))
  }

  test("Shema reference test 004"){
    this.runTest("references/schemas/test004.yml", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test 005"){
    this.runTest("references/schemas/test005.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 006"){
    this.runTest("references/schemas/test006.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 007"){
    this.runTest("references/schemas/test007.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 008"){
    this.runTest("references/schemas/test008.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 009"){
    this.runTest("references/schemas/test009.yml", Set("\n            \"$ref\": \"#/definitions/Type1\"", "\n            \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 010"){
    this.runTest("references/schemas/test010.yml", Set("\n            \"$ref\": \"#/definitions/Type1\"", "\n            \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 011"){
    this.runTest("references/schemas/test011.yml", Set(" \"#/definitions/Type1\"", " \"#/definitions/Type2\""))
  }

  test("Shema reference test 012"){
    this.runTest("references/schemas/test012.yml", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test 013"){
    this.runTest("references/schemas/test013.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 014"){
    this.runTest("references/schemas/test014.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 015"){
    this.runTest("references/schemas/test015.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 016"){
    this.runTest("references/schemas/test016.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 017"){
    this.runTest("references/schemas/test017.yml", Set("\n            \"$ref\": \"#/definitions/Type1\"", "\n            \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 018"){
    this.runTest("references/schemas/test018.yml", Set("\n            \"$ref\": \"#/definitions/Type1\"", "\n            \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 019"){
    this.runTest("references/schemas/test019.yml", Set(" \"#/definitions/Type1\"", " \"#/definitions/Type2\""))
  }

  test("Shema reference test 020"){
    this.runTest("references/schemas/test020.yml", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test 021"){
    this.runTest("references/schemas/test021.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 022"){
    this.runTest("references/schemas/test022.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 023"){
    this.runTest("references/schemas/test023.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 024"){
    this.runTest("references/schemas/test024.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 025"){
    this.runTest("references/schemas/test025.yml", Set("\n    \"$ref\": \"#/definitions/Type1\"", "\n    \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 026"){
    this.runTest("references/schemas/test026.yml", Set("\n    \"$ref\": \"#/definitions/Type1\"", "\n    \"$ref\": \"#/definitions/Type2\""))
  }

  test("Shema reference test 027"){
    this.runTest("references/schemas/test027.yml", Set(" \"#/definitions/Type1\"", " \"#/definitions/Type2\""))
  }

  test("Shema reference test 028"){
    this.runTest("references/schemas/test028.yml", Set("\"#/definitions/Type1\"", "\"#/definitions/Type2\""))
  }

  test("Shema reference test 029"){
    this.runTest("references/schemas/test029.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 030"){
    this.runTest("references/schemas/test030.yml", Set("#/definitions/Type1", "#/definitions/Type2"))
  }

  test("Shema reference test 031"){
    this.runTest("references/schemas/test031.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

  test("Shema reference test 032"){
    this.runTest("references/schemas/test032.yml", Set("/definitions/Type1", "/definitions/Type2"))
  }

//  test("Parameter reference test 001"){
//    this.runTest("references/parameters/test001.yml", Set("\"$ref\": \"#/parameters/param1\"", "\"$ref\": \"#/parameters/param2\"", "maxItems:", "exclusiveMinimum:", "required:", "collectionFormat:", "enum:", "items:\n          ", "exclusiveMaximum:", "type:", "minimum:", "default:", "maximum:", "pattern:", "multipleOf:", "description:", "allowEmptyValue:", "name:", "in:", "maxLength:", "minLength:", "minItems:", "\"$ref\":", "format:", "example:", "uniqueItems:"))
//  }

//  test("Parameter reference test 002"){
//    this.runTest("references/parameters/test002.yml", Set(" \"#/parameters/param1\"", " \"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 003"){
//    this.runTest("references/parameters/test003.yml", Set("\"#/parameters/param1\"", "\"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 004"){
//    this.runTest("references/parameters/test004.yml", Set(" \"#/parameters/param1\"", " \"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 005"){
//    this.runTest("references/parameters/test005.yml", Set("\"#/parameters/param1\"", "\"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 006"){
//    this.runTest("references/parameters/test006.yml", Set("#/parameters/param1", "#/parameters/param2"))
//  }
//
//  test("Parameter reference test 007"){
//    this.runTest("references/parameters/test007.yml", Set("#/parameters/param1", "#/parameters/param2"))
//  }
//
//  test("Parameter reference test 008"){
//    this.runTest("references/parameters/test008.yml", Set("/parameters/param1", "/parameters/param2"))
//  }
//
//  test("Parameter reference test 009"){
//    this.runTest("references/parameters/test009.yml", Set("/parameters/param1", "/parameters/param2"))
//  }

//  test("Parameter reference test 010"){
//    this.runTest("references/parameters/test010.yml", Set("\"$ref\": \"#/parameters/param1\"", "\"$ref\": \"#/parameters/param2\"", "maxItems:", "exclusiveMinimum:", "required:", "collectionFormat:", "enum:", "items:\n            ", "exclusiveMaximum:", "type:", "minimum:", "default:", "maximum:", "pattern:", "multipleOf:", "description:", "allowEmptyValue:", "name:", "in:", "maxLength:", "minLength:", "minItems:", "\"$ref\":", "format:", "example:", "uniqueItems:"))
//  }

//  test("Parameter reference test 011"){
//    this.runTest("references/parameters/test011.yml", Set(" \"#/parameters/param1\"", " \"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 012"){
//    this.runTest("references/parameters/test012.yml", Set("\"#/parameters/param1\"", "\"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 013"){
//    this.runTest("references/parameters/test013.yml", Set(" \"#/parameters/param1\"", " \"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 014"){
//    this.runTest("references/parameters/test014.yml", Set("\"#/parameters/param1\"", "\"#/parameters/param2\""))
//  }
//
//  test("Parameter reference test 015"){
//    this.runTest("references/parameters/test015.yml", Set("#/parameters/param1", "#/parameters/param2"))
//  }
//
//  test("Parameter reference test 016"){
//    this.runTest("references/parameters/test016.yml", Set("#/parameters/param1", "#/parameters/param2"))
//  }
//
//  test("Parameter reference test 017"){
//    this.runTest("references/parameters/test017.yml", Set("/parameters/param1", "/parameters/param2"))
//  }
//
//  test("Parameter reference test 018"){
//    this.runTest("references/parameters/test018.yml", Set("/parameters/param1", "/parameters/param2"))
//  }

 test("Response reference test 001"){
    this.runTest("references/responses/test001.yml", Set("\n          \"$ref\": \"#/responses/response1\"", "\n          \"$ref\": \"#/responses/response2\""))
  }

  test("Response reference test 002"){
    this.runTest("references/responses/test002.yml", Set("\n          \"$ref\": \"#/responses/response1\"", "\n          \"$ref\": \"#/responses/response2\""))
  }

  test("Response reference test 003"){
    this.runTest("references/responses/test003.yml", Set(" \"#/responses/response1\"", " \"#/responses/response2\""))
  }

  test("Response reference test 004"){
    this.runTest("references/responses/test004.yml", Set("\"#/responses/response1\"", "\"#/responses/response2\""))
  }

  test("Response reference test 005"){
    this.runTest("references/responses/test005.yml", Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test 006"){
    this.runTest("references/responses/test006.yml", Set("#/responses/response1", "#/responses/response2"))
  }

  test("Response reference test 007"){
    this.runTest("references/responses/test007.yml", Set("/responses/response1", "/responses/response2"))
  }

  test("Response reference test 008"){
    this.runTest("references/responses/test008.yml", Set("/responses/response1", "/responses/response2"))
  }

}
