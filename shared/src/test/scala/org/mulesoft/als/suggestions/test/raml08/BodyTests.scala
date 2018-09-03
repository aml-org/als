package org.mulesoft.als.suggestions.test.raml08

class BodyTests extends RAML08Test {

    test("Method with no bodies") {
        this.runTest("body/test001.raml",
            Set("application/json:",
                "application/xml:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:",
                "formParameters:",
                "description:",
                "schema:",
                "example:"))
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
                "formParameters:",
                "description:",
                "schema:",
                "example:"))
    }

    test("Response with some bodies") {
        this.runTest("body/test004.raml",
            Set("application/json:"))
    }
}
