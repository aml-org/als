package org.mulesoft.als.suggestions.test.raml10

class ExamplesTests extends RAML10Test {
	test("common test"){
		this.runTest("examples/test01.raml", Set("valueSeven", "valueEight"))
	}

	test("json test"){
		this.runTest("examples/test02.raml", Set("valueSeven", "valueEight"))
	}

    test("Object Property Test"){
        this.runTest("examples/test03.raml", Set("prop1:\n        ", "prop2:"))
    }
}