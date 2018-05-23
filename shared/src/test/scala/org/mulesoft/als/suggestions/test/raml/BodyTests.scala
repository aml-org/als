package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class BodyTests extends RAMLTest {

    test("test001") {
        this.runTest("body/test001.raml",
            Set("application/json:",
                "application/xml:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:"))
    }

    test("test002") {
        this.runTest("body/test002.raml",
            Set("application/json:",
                "multipart/formdata:",
                "application/x-www-form-urlencoded:"))
    }
}
