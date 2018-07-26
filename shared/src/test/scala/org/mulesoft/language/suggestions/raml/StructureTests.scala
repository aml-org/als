package org.mulesoft.language.suggestions.raml

class StructureTests extends RAMLSuggestionTest {

    test("test 01") {
        runTest("structure/test01.raml", Set("responses:"))
    }

    test("test 02") {
        runTest("structure/test02.raml", Set("types:"))
    }

    test("test 03") {
        runTest("structure/test03.raml", Set("resourceTypes"))
    }

    test("test 04") {
        runTest("structure/test04.raml", Set("title:"))
    }

    test("test 05") {
        runTest("structure/test05.raml", Set("traits:"))
    }

    test("test 06") {
        runTest("structure/test06.raml", Set("description:"))
    }

    test("test 07") {
        runTest("structure/test07.raml", Set("documentation:"))
    }

    test("test 08") {
        runTest("structure/test08.raml", Set("version:"))
    }

    test("test 09") {
        runTest("structure/test09.raml", Set("baseUri:", "baseUriParameters:"))
    }

    test("test 10") {
        runTest("structure/test10.raml", Set("protocols:"))
    }
}
