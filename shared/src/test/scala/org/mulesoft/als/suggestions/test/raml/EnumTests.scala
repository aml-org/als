package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class EnumTests extends RAMLTest {

    test("Security scheme types completion") {
        this.runTest("enums/test001.raml",
            Set("OAuth 1.0", "OAuth 2.0", "Basic Authentication", "Digest Authentication", "Pass Through", "x-{other}"))
    }

    test("NumberType format completion") {
        this.runTest("uses/testGroup01/test02.raml",
            Set("int32", "int64", "int", "long", "float", "double", "int16", "int8"))
    }
}
