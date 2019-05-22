package org.mulesoft.als.suggestions.test.raml10

class ExamplesStructureTests extends RAML10Test {


  test("Simple example filtered 01") {
    this.runTest(
      "structure/examples/example-filtered-01.raml",
      Set(
        "value: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  test("Simple example filtered 02") {
    this.runTest(
      "structure/examples/example-filtered-02.raml",
      Set(
        "value: "
      )
    )
  }

  test("Simple example") {
    this.runTest(
      "structure/examples/example-simple.raml",
      Set(
        "value: ",
        "description: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  test("Named examples") {
    this.runTest(
      "structure/examples/examples-named.raml",
      Set(
        "value: ",
        "description: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  test("Named examples filtered") {
    this.runTest(
      "structure/examples/examples-named-filtered.raml",
      Set(
        "value: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  test("Not Named example") {
    this.runTest(
      "structure/examples/examples-not-named.raml",
      Set()
    )
  }

  ignore("NamedExample fragment") { // Change in AMF 3.2.1
    this.runTest(
      "structure/examples/examples-fragment-01.raml",
      Set(
        "value: ",
        "description: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  test("NamedExample fragment without name") {
    this.runTest(
      "structure/examples/examples-fragment-not-named-01.raml",
      Set()
    )
  }

  test("NamedExample under value key") {
    this.runTest(
      "structure/examples/value-example.raml",
      Set()
    )
  }

  test("NamedExample under value name") {
    this.runTest(
      "structure/examples/examples-naming.raml",
      Set()
    )
  }
}
