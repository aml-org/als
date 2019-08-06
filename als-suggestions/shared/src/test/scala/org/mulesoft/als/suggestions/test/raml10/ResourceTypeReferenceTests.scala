package org.mulesoft.als.suggestions.test.raml10

class ResourceTypeReferenceTests extends RAML10Test {

  test("test001") {
    this.runSuggestionTest("resourceTypeReferences/test001.raml", Set("resourceType1", "resourceType2"))
  }

  test("test002") {
    this.runSuggestionTest("resourceTypeReferences/test002.raml", Set("resourceType1: ", "resourceType2: "))
  }

  test("test007") {
    this.runTest("resourceTypeReferences/test007.raml", Set("resourceType1", "resourceType2"))
  }

  test("test003") { // TODO: No multilines are allowed in completions. Replace for a snippet?
    this.runSuggestionTest(
      "resourceTypeReferences/test003.raml",
      Set("param1: ", "param2: ")
    )
  }

  test("test004") {
    this.runSuggestionTest(
      "resourceTypeReferences/test004.raml",
      Set("param1: ", "param2: ")
    )
  }

  test("test005") {
    this.runSuggestionTest("resourceTypeReferences/test005.raml", Set.empty)
  }

  test("test008") {
    this.runSuggestionTest("resourceTypeReferences/test008.raml", Set.empty) // what should do in this cases?
  }

  test("test006") {
    this.runSuggestionTest("resourceTypeReferences/test006.raml", Set("searchableColl: "))
  }
}
