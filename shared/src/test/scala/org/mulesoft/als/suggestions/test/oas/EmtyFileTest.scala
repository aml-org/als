package org.mulesoft.als.suggestions.test.oas

import org.mulesoft.als.suggestions.test.OASTest

class EmtyFileTest extends OASTest {

    test("Empty YAML file completion"){
        this.runTest("empty/file/empty.yml", Set("swagger: '2.0'"))
    }

    test("Empty JSON file completion"){
        this.runTest("empty/file/empty.json", Set("\"swagger\": \"2.0\""))
    }

}
