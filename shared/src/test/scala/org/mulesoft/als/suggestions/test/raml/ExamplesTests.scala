package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class ExamplesTests extends RAMLTest {
	test("common test"){
		this.runTest("examples/test01.raml", Set("valueSeven:", "valueEight:"))
	}
	
	test("json test"){
		this.runTest("examples/test02.raml", Set("valueSeven:", "valueEight:"))
	}
}