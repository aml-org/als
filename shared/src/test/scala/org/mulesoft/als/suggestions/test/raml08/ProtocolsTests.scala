package org.mulesoft.als.suggestions.test.raml08

class ProtocolsTests extends RAML08Test {

    test("Protocols test 01") {
        this.runTest("protocols/test01.raml", Set("[ HTTP ]", "[ HTTPS ]"))
    }

    test("Protocols test 02") {
        this.runTest("protocols/test02.raml", Set("[ HTTP ]", "[ HTTPS ]"))
    }

    test("Protocols test 03") {
        this.runTest("protocols/test03.raml", Set("HTTP", "HTTPS"))
    }

    test("Protocols test 04") {
        this.runTest("protocols/test04.raml", Set("HTTP", "HTTPS"))
    }

    test("Protocols test 05") {
        this.runTest("protocols/test05.raml", Set("HTTP"))
    }

    test("Protocols test 06") {
        this.runTest("protocols/test06.raml", Set("HTTP"))
    }

//    test("Protocols test 07") {
//        this.runTest("protocols/test07.raml", Set("HTTP"))
//    }

    test("Protocols test 08") {
        this.runTest("protocols/test08.raml", Set("HTTP"))
    }

    test("Protocols test 09") {
        this.runTest("protocols/test09.raml", Set("HTTP"))
    }

    test("Protocols test 10") {
        this.runTest("protocols/test10.raml", Set("[ HTTP ]", "[ HTTPS ]"))
    }
}
