package org.mulesoft.als.suggestions.test.raml10

class ExamplesTests extends RAML10Test {
	test("common test"){
		this.runTest("examples/test01.raml", Set("valueSeven", "valueEight"))
	}

	test("json test"){
		this.runTest("examples/test02.raml", Set("valueSeven", "valueEight"))
	}

	test("Object Property Test 1"){
        this.runTest("examples/test03.raml", Set("prop1:\n        ", "prop2:"))
    }

	test("Object Property Test 2"){
		this.runTest("examples/test04.raml", Set("prop2:"))
	}

	test("Object Property Test 3"){
		this.runTest("examples/test05.raml", Set("p2:"))
	}

	test("Object Property Test 4"){
		this.runTest("examples/test06.raml", Set("address"))
	}
}