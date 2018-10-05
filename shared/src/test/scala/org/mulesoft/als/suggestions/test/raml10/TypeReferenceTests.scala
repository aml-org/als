package org.mulesoft.als.suggestions.test.raml10

class TypeReferenceTests extends RAML10Test {

    test("TypeDeclaration shortcut 01") {
        this.runTest("typeReferences/test001.raml",
            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype2", "stype3"))
    }

    test("TypeDeclaration shortcut 02") {
        this.runTest("typeReferences/test002.raml",
            Set("stype2", "stype3", "string"))
    }

    test("TypeDeclaration explicit 01") {
        this.runTest("typeReferences/test003.raml",
            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype2", "stype3"))
    }

    test("TypeDeclaration explicit 02") {
        this.runTest("typeReferences/test004.raml",
            Set("stype2", "stype3", "string"))
    }

    test("Property type shortcut 01") {
        this.runTest("typeReferences/test005.raml",
            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype1", "stype2", "stype3"))
    }

    test("Property type shortcut 02") {
        this.runTest("typeReferences/test006.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Property type explicit 01") {
        this.runTest("typeReferences/test007.raml",
            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype1", "stype2", "stype3"))
    }

    test("Property type explicit 02") {
        this.runTest("typeReferences/test008.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

    test("Items type shortcut 01") {
        this.runTest("typeReferences/test009.raml",
            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype1", "stype2", "stype3"))
    }

    test("Items type shortcut 02") {
        this.runTest("typeReferences/test010.raml",
            Set("stype1", "stype2", "stype3", "string"))
    }

//    test("Items type explicit 01") {
//        this.runTest("typeReferences/test011.raml",
//            Set("nil", "boolean", "integer", "datetime", "date-only", "datetime-only", "file", "any", "number", "string", "time-only", "schema", "array", "object", "stype1", "stype2", "stype3"))
//    }
//
//    test("Items type explicit 02") {
//        this.runTest("typeReferences/test012.raml",
//            Set("stype1", "stype2", "stype3", "string"))
//    }
}
