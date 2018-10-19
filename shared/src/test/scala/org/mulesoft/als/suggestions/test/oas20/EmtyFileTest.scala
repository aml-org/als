package org.mulesoft.als.suggestions.test.oas20

class EmtyFileTest extends OAS20Test {

    test("Empty YAML file completion"){
        this.runTest("empty/file/empty.yml", Set("#%RAML 1.0", "#%RAML 0.8", "swagger: '2.0'"))
    }

    test("Empty JSON file completion"){
        this.runTest("empty/file/empty.json", Set("\"swagger\": \"2.0\""))
    }

}
