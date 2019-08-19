package org.mulesoft.als.suggestions.test.raml10

class ExamplesStructureTests extends RAML10Test {


  ignore("Simple example filtered 01") {
    this.runSuggestionTest(
      "structure/examples/example-filtered-01.raml",
      Set(
        "value: ",
        "strict: ",
        "displayName: ",
        "annotations: ",
      )
    )
  }

  ignore("Simple example filtered 02") {
    this.runSuggestionTest(
      "structure/examples/example-filtered-02.raml",
      Set(
        "value: "
      )
    )
  }

  test("Simple example") {
    this.runSuggestionTest(
      "structure/examples/example-simple.raml",
      Set(
        "value: ",
        "description: ",
        "strict: ",
        "displayName: ",
      )
    )
  }

  test("Named examples") {
    this.runSuggestionTest(
      "structure/examples/examples-named.raml",
      Set(
        "value: ",
        "description: ",
        "strict: ",
        "displayName: ",
      )
    )
  }

  ignore("Named examples filtered") {
    this.runSuggestionTest(
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
    this.runSuggestionTest(
      "structure/examples/examples-not-named.raml",
      Set()
    )
  }

  ignore("NamedExample fragment") { // Change in AMF 3.2.1
    this.runSuggestionTest(
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

  ignore("NamedExample fragment without name") {
    this.runSuggestionTest(
      "structure/examples/examples-fragment-not-named-01.raml",
      Set()
    )
  }

  test("NamedExample under value key") {
    this.runSuggestionTest(
      "structure/examples/value-example.raml",
      Set()
    )
  }

  test("NamedExample under value name") {
    this.runSuggestionTest(
      "structure/examples/examples-naming.raml",
      Set()
    )
  }
}
