package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class FragmentTests extends AMLSuggestionsTest {

    def rootPath:String = "AML/AsyncAPI"

    test("test001"){
        this.runTest("fragment/test001.yaml", "dialect6.yaml", Set("externalDocs:\n  ", "description:", "headers:\n  ", "tags:\n  "))
    }

    test("test002"){
        this.runTest("fragment/test002.yaml", "dialect6.yaml", Set("pattern:", "maxItems:", "required:\n    ", "items:\n    ", "exclusiveMaximum:", "\"$schema\":", "type:\n    ", "xml:\n    ", "key:", "minimum:", "maximum:", "default:\n    ", "exclusiveMinimum:", "multipleOf:", "description:", "minProperties:", "patternProperties:\n    ", "maxLength:", "title:", "minLength:", "minItems:", "additionalItems:", "id:", "uniqueItems:"))
    }

    test("test003"){
        this.runTest("fragment/test003.yaml", "dialect6.yaml", Set())
    }

    test("test004"){
        this.runTest("fragment/test004.yaml", "dialect6.yaml", Set("[ null ]", "[ boolean ]", "[ string ]", "[ array ]", "[ object ]", "[ number ]", "[ integer ]"))
    }

//    test("test005"){
//        this.runTest("fragment/test005.yaml", "dialect6.yaml", Set("null", "boolean", "string", "array", "object", "number", "integer"))
//    }

    test("test006"){
        this.runTest("fragment/test006.yaml", "dialect6.yaml", Set("pattern:", "maxItems:", "required:\n    ", "items:\n    ", "exclusiveMaximum:", "\"$schema\":", "type:\n    ", "xml:\n    ", "key:", "minimum:", "maximum:", "default:\n    ", "exclusiveMinimum:", "multipleOf:", "description:", "minProperties:", "patternProperties:\n    ", "maxLength:", "title:", "minLength:", "minItems:", "additionalItems:", "id:", "uniqueItems:"))
    }

    test("test007"){
        this.runTest("fragment/test007.yaml", "dialect6.yaml", Set("name:", "description:"))
    }
}

