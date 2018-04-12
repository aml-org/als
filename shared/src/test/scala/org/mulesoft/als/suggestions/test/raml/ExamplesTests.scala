package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class ExamplesTests extends RAMLTest {
	test("test"){
		this.runTest("examples/test01.raml", Set("valueSeven", "valueEight"))
	}
}