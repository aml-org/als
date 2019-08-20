package org.mulesoft.als.suggestions.test.raml10

class TraitReferenceTests extends RAML10Test {

  test("test001") {
    this.runSuggestionTest("traitReferences/test001.raml", Set("trait1", "trait2"))
  }

  test("test002") {
    this.runSuggestionTest("traitReferences/test002.raml", Set("trait1", "trait2"))
  }

  test("test003") {
    this.runSuggestionTest("traitReferences/test003.raml", Set("trait2"))
  }

  test("test004") {
    this.runSuggestionTest("traitReferences/test004.raml", Set("trait1:\n        ", "trait2:\n        "))
  }

  test("test005") {
    this.runSuggestionTest("traitReferences/test005.raml", Set("trait1:\n        ", "trait2:\n        "))
  }

  test("test006") {
    this.runSuggestionTest("traitReferences/test006.raml", Set("trait2:\n        "))
  }

  test("test007") {
    this.runSuggestionTest(
      "traitReferences/test007.raml",
      Set("trait1", "trait2")
    )
  }

  test("test008") {
    this.runSuggestionTest("traitReferences/test008.raml", Set.empty)
  }

  test("test009") {
    this.runSuggestionTest("traitReferences/test009.raml", Set("param1: ", "param2: "))
  }

  test("test010") {
    this.runSuggestionTest(
      "traitReferences/test010.raml",
      Set.empty
    )
  }

  test("test011") {
    this.runSuggestionTest(
      "traitReferences/test011.raml",
      Set("param2: ")
    )
  }

  test("test012") {
    this.runSuggestionTest("traitReferences/test012.raml", Set("param1: ", "param2: "))
  }

  test("Trait Fragment last operation") {
    this.runSuggestionTest(
      "traitReferences/test-trait-external-01.raml",
      Set("number",
          "any",
          "date-only",
          "time-only",
          "datetime",
          "string",
          "datetime-only",
          "object",
          "nil",
          "array",
          "boolean",
          "file",
          "integer")
    )

  }

  test("Trait Fragment second to last operation") {
    this.runSuggestionTest(
      "traitReferences/test-trait-external-02.raml",
      Set("number",
          "any",
          "date-only",
          "time-only",
          "datetime",
          "string",
          "datetime-only",
          "object",
          "nil",
          "array",
          "boolean",
          "file",
          "integer")
    )
  }

//
//  test("test013") {
//    this.runSuggestionTest("traitReferences/test013.raml",
//        Set("param1", "param2"))
//  }
//
//  test("test014") {
//    this.runTest("traitReferences/test014.raml",
//        Set("param1", "param2"))
//  }
//
//  test("test015") {
//    this.runTest("traitReferences/test015.raml",
//        Set("param1", "param2"))
//  }
//
//  test("test016") {
//    this.runTest("traitReferences/test016.raml",
//        Set("param2"))
//  }
//
//  test("test017") {
//    this.runTest("traitReferences/test017.raml",
//        Set("param2"))
//  }
//
//  test("test018") {
//    this.runTest("traitReferences/test018.raml",
//        Set("param2"))
//  }

}
