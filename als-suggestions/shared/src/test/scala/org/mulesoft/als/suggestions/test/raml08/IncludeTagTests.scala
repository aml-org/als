package org.mulesoft.als.suggestions.test.raml08

class IncludeTagTests extends RAML08Test {

    test("test001") {
        this.runTest("includeTag/test001.raml",
            Set("!include"))
    }

    test("test002") {
        this.runTest("includeTag/test002.raml",
            Set("!include"))
    }

//    test("test003") {
//        this.runTest("includeTag/test003.raml",
//            Set("!include"))
//    }

    test("test004") {
        this.runTest("includeTag/test004.raml",
            Set("!include"))
    }
}
