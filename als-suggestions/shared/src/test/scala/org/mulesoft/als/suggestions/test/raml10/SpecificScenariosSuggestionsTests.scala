package org.mulesoft.als.suggestions.test.raml10

class SpecificScenariosSuggestionsTests extends RAML10Test {

  ignore("MediaType as Single") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType01.raml",
                           Set("application/json", "application/xml", "application/x-www-form-urlencoded"))
  }

  ignore("MediaType as Sequence") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType02.raml",
                           Set("application/json", "application/x-www-form-urlencoded"))
  }

  ignore("MediaType as Open Sequence") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType03.raml",
                           Set("application/json", "application/x-www-form-urlencoded", "application/xml"))
  }

  test("MediaType Suggestions after '/' character in composed words 01") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType04.raml", Set("json:\n            "))
  }

  ignore("MediaType Suggestions after '/' character in composed words 02") {
    this.runSuggestionTest(
      "specific-scenarios/mediaType/mediaType05.raml",
      Set(
        "application/json:\n            ",
        "application/xml:\n            ",
        "example:\n            ",
        "properties:\n            ",
        "xml:\n            ",
        "facets:\n            ",
        "examples:\n            ",
        "description: ",
        "displayName: ",
        "type: ",
        "enum: ",
        "schema: ",
        "default: ",
        "items: "
      )
    )
  }

  ignore("ALS-MediaType Suggestions after '/' character in composed words 03") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType06.raml",
                           Set("json:\n              ", "xml:\n              "))
  }

  test("MediaType Suggestions after '/' character in composed words 04") {
    this.runSuggestionTest("specific-scenarios/mediaType/mediaType07.raml", Set())
  }

  test("Library Types Suggestions") {
    this.runSuggestionTest("specific-scenarios/libraryTypes/api.raml", Set("Employee", "Person", "Manager"))
  }

  test("Library Suggestions after '/'") {
    this.runSuggestionTest("specific-scenarios/librariesPaths/test01.raml", Set("library01.raml"))
  }

  test("Multiple Library Suggestions 01") {
    this.runSuggestionTest("specific-scenarios/multipleLibraryTypes/test01.raml", Set("Employee", "Person", "Manager"))
  }

  test("Multiple Library Suggestions 02") {
    this.runSuggestionTest("specific-scenarios/multipleLibraryTypes/test02.raml",
                           Set("Employee2", "Person2", "Manager2"))
  }

  test("Check prefix in included directory") {
    this.runSuggestionTest("specific-scenarios/afterSlash/api.raml", Set("dataType.raml"))
  }

  test("Method Body values") {
    this.runSuggestionTest("specific-scenarios/postValues/test01.raml", Set())
  }

  test("Category matching test 01") {
    this.runTestCategory("specific-scenarios/category/test01.raml")
  }

  test("Category matching test 02") {
    this.runTestCategory("specific-scenarios/category/test02.raml")
  }

  test("Category matching test 03") {
    this.runTestCategory("specific-scenarios/category/test03.raml")
  }

  test("Trait - Type suggestion") {
    this.runSuggestionTest(
      "specific-scenarios/traitCompletions/test06.raml",
      Set(
        "number",
        "any",
        "union",
        "date-only",
        "time-only",
        "datetime",
        "ProcessVariableList",
        "ProcessModel",
        "string",
        "datetime-only",
        "object",
        "nil",
        "array",
        "version",
        "boolean",
        "file",
        "integer",
        "Process",
        "Task"
      )
    )
  }
}
