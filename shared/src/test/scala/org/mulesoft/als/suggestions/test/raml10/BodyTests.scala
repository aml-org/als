package org.mulesoft.als.suggestions.test.raml10

class BodyTests extends RAML10Test {

    test("Method with no bodies") {
        this.runTest("body/test001.raml",
            Set("application/json:",
                "application/xml:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:",
                "displayName:",
                "type:",
                "xml:",
                "default:",
                "description:",
                "schema:",
                "examples:",
                "example:",
                "facets:",
                "properties:"))
    }

    test("Method with some bodies") {
        this.runTest("body/test002.raml",
            Set("application/json:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:"))
    }

    test("Response with no bodies") {
        this.runTest("body/test003.raml",
            Set("application/json:",
                "application/xml:",
                "displayName:",
                "type:",
                "xml:",
                "default:",
                "description:",
                "schema:",
                "examples:",
                "example:",
                "facets:",
                "properties:"))
    }

    test("Response with some bodies") {
        this.runTest("body/test004.raml",
            Set("application/json:"))
    }

    test("Method body type shortcut") {
        this.runTest("body/test005.raml",
            Set("boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "nil", "schema", "array", "object", "A", "B"))
    }

    test("Response body type shortcut") {
        this.runTest("body/test006.raml",
            Set("boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "nil", "schema", "array", "object", "A", "B"))
    }
}
