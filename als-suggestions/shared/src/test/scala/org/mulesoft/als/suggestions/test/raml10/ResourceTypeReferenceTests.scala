package org.mulesoft.als.suggestions.test.raml10

class ResourceTypeReferenceTests extends RAML10Test {

  test("test001") {
    this.runTest("resourceTypeReferences/test001.raml", Set("resourceType1", "resourceType2"))
  }

  test("test002") {
    this.runTest("resourceTypeReferences/test002.raml", Set("resourceType1: ", "resourceType2: "))
  }

  test("test003") { // TODO: No multilines are allowed in completions. Replace for a snippet?
    this.runTest(
      "resourceTypeReferences/test003.raml",
      Set(
        """
            |    resourceType1:
            |      param1:
            |      param2: """.stripMargin,
        """
            |    resourceType2:
            |      param1:
            |      param2: """.stripMargin
      )
    )
  }

  test("test004") { // TODO: No multilines are allowed in completions. Replace for a snippet?
    this.runTest(
      "resourceTypeReferences/test004.raml",
      Set(
        """resourceType1:
            |      param1:
            |      param2: """.stripMargin,
        """resourceType2:
            |      param1:
            |      param2: """.stripMargin
      )
    )
  }

  test("test005") {
    this.runTest("resourceTypeReferences/test005.raml",
                 Set("{ resourceType1: {  param1 : ,  param2 : } }", "{ resourceType2: {  param1 : ,  param2 : } }"))
  }
}
