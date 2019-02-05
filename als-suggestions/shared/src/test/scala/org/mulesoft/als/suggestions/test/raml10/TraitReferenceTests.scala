package org.mulesoft.als.suggestions.test.raml10

class TraitReferenceTests extends RAML10Test {

  test("test001") {
    this.runTest("traitReferences/test001.raml", Set("[ trait1 ]", "[ trait2 ]"))
  }

  test("test002") {
    this.runTest("traitReferences/test002.raml", Set("trait1", "trait2"))
  }

  test("test003") {
    this.runTest("traitReferences/test003.raml", Set("trait2"))
  }

  test("test004") {
    this.runTest("traitReferences/test004.raml", Set("- trait1", "- trait2"))
  }

  test("test005") {
    this.runTest("traitReferences/test005.raml", Set("trait1", "trait2"))
  }

  test("test006") {
    this.runTest("traitReferences/test006.raml", Set("trait2"))
  }

  test("test007") {
    this.runTest(
      "traitReferences/test007.raml",
      Set(
        """
            |      - trait1:
            |          param1:
            |          param2:""".stripMargin,
        """
            |      - trait2:
            |          param1:
            |          param2:""".stripMargin
      )
    )
  }

  test("test008") {
    this.runTest("traitReferences/test008.raml",
                 Set("{ trait1: {  param1 : ,  param2 : } }", "{ trait2: {  param1 : ,  param2 : } }"))
  }

  test("test009") {
    this.runTest("traitReferences/test009.raml", Set("{ trait2: {  param1 : ,  param2 : } }"))
  }

  test("test010") {
    this.runTest(
      "traitReferences/test010.raml",
      Set(
        """- trait1:
              |          param1:
              |          param2:""".stripMargin,
        """- trait2:
              |          param1:
              |          param2:""".stripMargin
      )
    )
  }

  test("test011") {
    this.runTest(
      "traitReferences/test011.raml",
      Set(
        """trait1:
              |          param1:
              |          param2:""".stripMargin,
        """trait2:
              |          param1:
              |          param2:""".stripMargin
      )
    )
  }

  test("test012") {
    this.runTest("traitReferences/test012.raml",
                 Set("""trait2:
              |          param1:
              |          param2:""".stripMargin))
  }

  test("Trait Fragment last operation") {
    this.runTest(
      "traitReferences/test-trait-external-01.raml",
      Set("number",
          "any",
          "union",
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
    this.runTest(
      "traitReferences/test-trait-external-02.raml",
      Set("number",
          "any",
          "union",
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
//    this.runTest("traitReferences/test013.raml",
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
