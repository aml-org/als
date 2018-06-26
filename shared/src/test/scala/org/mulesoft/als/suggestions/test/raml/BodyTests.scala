package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class BodyTests extends RAMLTest {

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

    test("Response with some bodies") {
        this.runTest("body/test002.raml",
            Set("application/json:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:"))
    }
}
