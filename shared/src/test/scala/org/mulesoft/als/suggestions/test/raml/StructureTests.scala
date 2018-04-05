package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class StructureTests extends RAMLTest {
	test("test") {
		this.runTest("test01.raml", Set("responses"));
	}
	
	test("facets test") {
		this.runTest("facets/test01.raml", Set("testFacet1", "testFacet2", "testFacet3", "testFacet5"));
	}
	
	test("methods test") {
		this.runTest("methods/test01.raml", Set("displayName", "type", "description", "get", "put", "post", "delete", "options", "head", "patch", "trace", "connect"));
	}
}
