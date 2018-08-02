package org.mulesoft.als.suggestions.test.raml10

class TypeReferenceTests extends RAML10Test {

    test("TypeDeclaration shortcut") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("TypeDeclaration explicit") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Property type shortcut") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Property type explicit") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Items type shortcut") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Items type explicit") {
        this.runTest("typeReferences/test001.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }
}
