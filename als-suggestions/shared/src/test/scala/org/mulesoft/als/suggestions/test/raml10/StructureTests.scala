package org.mulesoft.als.suggestions.test.raml10

import org.mulesoft.als.suggestions.plugins.raml.CommonHeaderNames

class StructureTests extends RAML10Test {

  test("StructureTests responses") {
    this.runSuggestionTest("structure/test01.raml", Set("responses:\n      "))
  }

  test("StructureTests root node types") {
    this.runSuggestionTest("structure/test02.raml", Set("types:\n  "))
  }

  test("StructureTests root node resourceTypes") {
    this.runSuggestionTest("structure/test03.raml", Set("resourceTypes"))
  }

  test("StructureTests root node title") {
    this.runSuggestionTest("structure/test04.raml", Set("title: "))
  }

  test("StructureTests root node traits") {
    this.runSuggestionTest("structure/test05.raml", Set("traits:\n  "))
  }

  test("StructureTests root node description") {
    this.runSuggestionTest("structure/test06.raml", Set("description: "))
  }

  test("StructureTests root node documentation") {
    this.runSuggestionTest("structure/test07.raml", Set("documentation:\n  "))
  }

  test("StructureTests root node version") {
    this.runSuggestionTest("structure/test08.raml", Set("version: "))
  }

  test("StructureTests root node baseUri") {
    this.runSuggestionTest("structure/test09.raml", Set("baseUri: ", "baseUriParameters:\n  "))
  }

  test("StructureTests root node protocols") {
    this.runSuggestionTest("structure/test10.raml", Set("protocols: "))
  }

  test("StructureTests root node mediaType") {
    this.runSuggestionTest("structure/test11.raml", Set("mediaType: "))
  }

  test("StructureTests root node schemas") {
    this.runSuggestionTest("structure/test12.raml", Set("schemas:\n  "))
  }

  test("StructureTests root node securitySchemes") {
    this.runSuggestionTest("structure/test13.raml", Set("securitySchemes:\n  "))
  }

  test("StructureTests root node securedBy") {
    this.runSuggestionTest("structure/test14.raml", Set("securedBy: "))
  }

  test("StructureTests root node annotationTypes") {
    this.runSuggestionTest("structure/test15.raml", Set("annotationTypes:\n  "))
  }

  test("StructureTests root node uses") {
    this.runSuggestionTest("structure/test16.raml", Set("uses:\n  "))
  }

  test("StructureTests node method body") {
    this.runSuggestionTest("structure/test18.raml", Set("body:\n      "))
  }

  test("StructureTests resource node is") {
    this.runSuggestionTest("structure/test21.raml", Set("is:\n    "))
  }

  test("StructureTests resource node type") {
    this.runSuggestionTest("structure/test22.raml", Set("type:\n    "))
  }

  test("StructureTests resource node description") {
    this.runSuggestionTest("structure/test23.raml", Set("description: "))
  }

  test("StructureTests resource node displayName") {
    this.runSuggestionTest("structure/test24.raml", Set("displayName: "))
  }

  test("StructureTests resource node put, post, patch") {
    this.runSuggestionTest("structure/test26.raml", Set("put:\n    ", "post:\n    ", "patch:\n    "))
  }

  test("StructureTests resource node uriParameters") {
    this.runSuggestionTest("structure/test27.raml", Set("uriParameters:\n    "))
  }

  test("StructureTests resource node options") {
    this.runSuggestionTest("structure/test28.raml", Set("options:\n    "))
  }

  test("StructureTests resource node head") {
    this.runSuggestionTest("structure/test29.raml", Set("head:\n    "))
  }

  test("StructureTests resource node trace") {
    this.runSuggestionTest("structure/test30.raml", Set("trace:\n    "))
  }

  test("StructureTests resource node connect") {
    this.runSuggestionTest("structure/test31.raml", Set("connect:\n    "))
  }

  test("StructureTests method node queryString, queryParameters") {
    this.runSuggestionTest("structure/test32.raml", Set("queryString:\n      ", "queryParameters:\n      "))
  }

  test("StructureTests method node headers") {
    this.runSuggestionTest("structure/test33.raml", Set("headers:\n      "))
  }

  test("StructureTests method node response description") {
    this.runSuggestionTest("structure/test34.raml", Set("description: "))
  }

  test("StructureTests method node responses") {
    this.runSuggestionTest("structure/test35.raml", Set("responses:\n      "))
  }

  test("StructureTests method node is") {
    this.runSuggestionTest("structure/test37.raml", Set("is:\n      "))
  }

  test("StructureTests method node description") {
    this.runSuggestionTest("structure/test38.raml", Set("description: "))
  }

  test("StructureTests method node displayName") {
    this.runSuggestionTest("structure/test39.raml", Set("displayName: "))
  }

  test("StructureTests method node response headers") {
    this.runSuggestionTest("structure/test41.raml", Set("headers:\n          "))
  }

  test("StructureTests method node response body") {
    this.runSuggestionTest("structure/test42.raml", Set("body:\n          "))
  }

  test("StructureTests resourceType node is") {
    this.runSuggestionTest("structure/test44.raml", Set("is:\n      "))
  }

  test("StructureTests resourceType node type") {
    this.runSuggestionTest("structure/test45.raml", Set("type:\n      "))
  }

  test("StructureTests resourceType node description") {
    this.runSuggestionTest("structure/test46.raml", Set("description: "))
  }

  test("StructureTests resourceType node displayName") {
    this.runSuggestionTest("structure/test47.raml", Set("displayName: "))
  }

  test("StructureTests resourceType operation nodes suggestions") {
    this.runSuggestionTest("structure/test163.raml", Set("responses"))
  }

  test("StructureTests resourceType in fragment operation nodes suggestions") {
    this.runSuggestionTest("structure/test163.raml", Set("responses"))
  }

  test("StructureTests resourceType node put, post, patch") {
    this.runSuggestionTest("structure/test49.raml", Set("put:\n      ", "post:\n      ", "patch:\n      "))
  }

  test("StructureTests resourceType node uriParameters") {
    this.runSuggestionTest("structure/test50.raml", Set("uriParameters:\n      "))
  }

  test("StructureTests resourceType node options") {
    this.runSuggestionTest("structure/test51.raml", Set("options:\n      "))
  }

  test("StructureTests resourceType node head") {
    this.runSuggestionTest("structure/test52.raml", Set("head:\n      "))
  }

  test("StructureTests resourceType node trace") {
    this.runSuggestionTest("structure/test53.raml", Set("trace:\n      "))
  }

  test("StructureTests resourceType node connect") {
    this.runSuggestionTest("structure/test54.raml", Set("connect:\n      "))
  }

  test("StructureTests resourceType node usage") {
    this.runSuggestionTest("structure/test55.raml", Set("usage: "))
  }

  test("StructureTests trait node queryString, queryParameters") {
    this.runSuggestionTest("structure/test57.raml", Set("queryString:\n      ", "queryParameters:\n      "))
  }

  test("StructureTests trait node headers") {
    this.runSuggestionTest("structure/test58.raml", Set("headers:\n      "))
  }

  test("StructureTests trait node responses") {
    this.runSuggestionTest("structure/test60.raml", Set("responses:\n      "))
  }

  test("StructureTests trait node description") {
    this.runSuggestionTest("structure/test63.raml", Set("description: "))
  }

  test("StructureTests trait node displayName") {
    this.runSuggestionTest("structure/test64.raml", Set("displayName: "))
  }

  test("StructureTests trait node body") {
    this.runSuggestionTest("structure/test69.raml", Set("body:\n      "))
  }

  test("StructureTests Security Scheme Declaration node type") {
    this.runSuggestionTest("structure/test83.raml", Set("type: "))
  }

  test("StructureTests Security Scheme Declaration node settings ") {
    this.runSuggestionTest("structure/test84.raml", Set("settings:\n      "))
  }

  test("StructureTests Security Scheme Declaration node OAuth 2.0 settings accessTokenUri") {
    this.runSuggestionTest("structure/test86.raml", Set("accessTokenUri: "))
  }

  test("StructureTests Security Scheme Declaration node description") {
    this.runSuggestionTest("structure/test89.raml", Set("description: "))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node headers") {
    this.runSuggestionTest("structure/test91.raml", Set("headers:\n        "))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node responses") {
    this.runSuggestionTest("structure/test93.raml", Set("responses:\n        "))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node requestTokenUri") {
    this.runSuggestionTest("structure/test94.raml", Set("requestTokenUri: "))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node tokenCredentialsUri") {
    this.runSuggestionTest("structure/test96.raml", Set("tokenCredentialsUri: "))
  }

  test("StructureTests Security Scheme Declaration node displayName") {
    this.runSuggestionTest("structure/test98.raml", Set("displayName: "))
  }

  test("Declaring Annotation Type facet 'allowedTargets'") {
    this.runSuggestionTest("structure/test99.raml", Set("allowedTargets:\n      "))
  }

  test("Declaring Annotation Type facet minLength completion") {
    this.runSuggestionTest("structure/test100.raml", Set("minLength: "))
  }

  test("Declaring Annotation Type facet maxLength completion") {
    this.runSuggestionTest("structure/test101.raml", Set("maxLength: "))
  }

  test("Declaring Annotation Type facet example, examples completion") {
    this.runSuggestionTest("structure/test102.raml", Set("example:\n      ", "examples:\n      "))
  }

  test("Declaring Annotation Type facet default completion") {
    this.runSuggestionTest("structure/test104.raml", Set("default: "))
  }

  test("Declaring Annotation Type facet displayName completion") {
    this.runSuggestionTest("structure/test105.raml", Set("displayName: "))
  }

  test("Declaring Annotation Type facet description completion") {
    this.runSuggestionTest("structure/test106.raml", Set("description: "))
  }

  test("Declaring Annotation Type facet required completion") {
    this.runSuggestionTest("structure/test107.raml", Set())
  }

  test("Declaring Annotation Type facet repeat completion") {
    this.runSuggestionTest("structure/test108.raml", Set())
  }

  test("Declaring Annotation Type facet pattern completion") {
    this.runSuggestionTest("structure/test109.raml", Set("pattern: "))
  }

  test("Declaring Annotation Type facet type completion") {
    this.runSuggestionTest("structure/test110.raml", Set("type:\n      "))
  }

  test("Declaring Annotation Type facet properties completion") {
    this.runSuggestionTest("structure/test111.raml", Set("properties:\n      "))
  }

  test("Declaring Annotation Type facet schema completion") {
    this.runSuggestionTest("structure/test112.raml", Set("schema: "))
  }

  test("Declaring Annotation Type facet facets completion") {
    this.runSuggestionTest("structure/test113.raml", Set("facets:\n      "))
  }

  test("Declaring Annotation Type facet xml completion") {
    this.runSuggestionTest("structure/test114.raml", Set("xml:\n      "))
  }

  test("Type facet minLength completion") {
    this.runSuggestionTest("structure/test115.raml", Set("minLength: "))
  }

  test("Type facet maxLength completion") {
    this.runSuggestionTest("structure/test116.raml", Set("maxLength: "))
  }

  test("Type facet example, examples completion") {
    this.runSuggestionTest("structure/test117.raml", Set("example:\n      ", "examples:\n      "))
  }

  test("Type facet default completion") {
    this.runSuggestionTest("structure/test119.raml", Set("default: "))
  }

  test("Type facet displayName completion") {
    this.runSuggestionTest("structure/test120.raml", Set("displayName: "))
  }

  test("Type facet description completion") {
    this.runSuggestionTest("structure/test121.raml", Set("description: "))
  }

  test("Type facet required completion") {
    this.runSuggestionTest("structure/test122.raml", Set())
  }

  test("Type facet repeat completion") {
    this.runSuggestionTest("structure/test123.raml", Set())
  }

  test("Type facet pattern completion") {
    this.runSuggestionTest("structure/test124.raml", Set("pattern: "))
  }

  test("Type facet type completion") {
    this.runSuggestionTest("structure/test125.raml", Set("type:\n      "))
  }

  test("Type facet schema completion") {
    this.runSuggestionTest("structure/test127.raml", Set("schema: "))
  }

  test("Type facet facets completion") {
    this.runSuggestionTest("structure/test128.raml", Set("facets:\n      "))
  }

  test("Type facet xml completion") {
    this.runSuggestionTest("structure/test129.raml", Set("xml:\n      "))
  }

  test("Parameter facet minLength completion") {
    this.runSuggestionTest("structure/test130.raml", Set("minLength: "))
  }

  test("Parameter facet maxLength completion") {
    this.runSuggestionTest("structure/test131.raml", Set("maxLength: "))
  }

  test("Parameter facet example, examples completion") {
    this.runSuggestionTest("structure/test132.raml", Set("example:\n          ", "examples:\n          "))
  }

  test("Parameter facet default completion") {
    this.runSuggestionTest("structure/test134.raml", Set("default: "))
  }

  test("Parameter facet displayName completion") {
    this.runSuggestionTest("structure/test135.raml", Set("displayName: "))
  }

  test("Parameter facet description completion") {
    this.runSuggestionTest("structure/test136.raml", Set("description: "))
  }

  test("Parameter facet repeat completion") {
    this.runSuggestionTest("structure/test138.raml", Set())
  }

  test("Parameter facet pattern completion") {
    this.runSuggestionTest("structure/test139.raml", Set("pattern: "))
  }

  test("Parameter facet type completion") {
    this.runSuggestionTest("structure/test140.raml", Set("type:\n          "))
  }

  test("Parameter facet schema completion") {
    this.runSuggestionTest("structure/test141.raml", Set("schema: "))
  }

  test("Parameter facet facets completion") {
    this.runSuggestionTest("structure/test142.raml", Set("facets:\n          "))
  }

  test("Parameter facet xml completion") {
    this.runSuggestionTest("structure/test143.raml", Set("xml:\n          "))
  }

  test("discriminator") {
    this.runSuggestionTest("structure/test145.raml", Set("one", "two"))
  }

  test("documentation item fields") {
    this.runSuggestionTest("structure/test146.raml", Set("title: ", "content: "))
  }

  test("documentation item 'title'") {
    this.runSuggestionTest("structure/test147.raml", Set("title: "))
  }

  test("documentation item 'content'") {
    this.runSuggestionTest("structure/test148.raml", Set("content: "))
  }

  test("documentation item 'content' after 'title'") {
    this.runSuggestionTest("structure/test149.raml", Set("content: "))
  }

  test("documentation item 'title' before 'content'") {
    this.runSuggestionTest("structure/test150.raml", Set("title: "))
  }

  test("cursor on single value property") {
    this.runSuggestionTest(
      "structure/test151.raml",
      Set(
        "baseUri",
        "baseUriParameters",
        "traits",
        "resourceTypes",
        "types",
        "annotationTypes",
        "documentation",
        "mediaType",
        "schemas",
        "description",
        "protocols",
        "securitySchemes",
        "securedBy",
        "version",
        "uses"
      )
    )
  }

  test("StructureTests duplicates") {
    this.runSuggestionTest(
      "structure/test152.raml",
      Set(
        "enum:\n          ",
        "displayName: ",
        "additionalProperties: ",
        "xml:\n          ",
        "discriminator: ",
        "default: ",
        "maxProperties: ",
        "minProperties: ",
        "description: ",
        "discriminatorValue: ",
        "examples:\n          ",
        "facets:\n          ",
        "example:\n          "
      )
    )
  }

  test("Type property 'required' completion") {
    this.runSuggestionTest("structure/test153.raml", Set("required: "))
  }

  //TODO: What is the utility of this test below? is this good behaviour?
  test("Query parameter with comment") {
    this.runSuggestionTest(
      "structure/test154.raml",
      Set("required",
          "displayName",
          "type",
          "xml",
          "default",
          "pattern",
          "maxLength",
          "minLength",
          "examples",
          "schema",
          "facets",
          "properties",
          "description",
          "items")
    )
  }

  test("StructureTests schema 1") {
    this.runSuggestionTest(
      "structure/test155.raml",
      Set(
        "enum: ",
        "displayName: ",
        "xml:\n        ",
        "default: ",
        "pattern: ",
        "description: ",
        "maxLength: ",
        "minLength: ",
        "examples:\n        ",
        "facets:\n        ",
        "example:\n        ",
        "properties:\n        ",
        "items: "
      )
    )
  }

  test("StructureTests schema 2") {
    this.runSuggestionTest(
      "structure/test156.raml",
      Set(
        "application/json:\n        ",
        "application/xml:\n        ",
        "multipart/form-data:\n        ",
        "application/x-www-form-urlencoded:\n        ",
        "displayName: ",
        "type: ",
        "enum: ",
        "xml:\n        ",
        "default: ",
        "description: ",
        "schema: ",
        "examples:\n        ",
        "example:\n        ",
        "facets:\n        ",
        "properties:\n        ",
        "items: "
      )
    )
  }

  test("Base URI parameter completion") {
    this.runSuggestionTest(
      "structure/test157.raml",
      Set(
        "enum: ",
        "displayName: ",
        "type: ",
        "xml:\n      ",
        "default: ",
        "pattern: ",
        "description: ",
        "maxLength: ",
        "minLength: ",
        "examples:\n      ",
        "schema: ",
        "facets:\n      ",
        "example:\n      ",
        "properties:\n      ",
        "items: "
      )
    )
  }

  test("Header completion") {
    this.runSuggestionTest(
      "structure/test158.raml",
      Set(
        "required: ",
        "enum: ",
        "displayName: ",
        "type: ",
        "xml:\n              ",
        "default: ",
        "pattern: ",
        "description: ",
        "maxLength: ",
        "minLength: ",
        "examples:\n              ",
        "schema: ",
        "facets:\n              ",
        "example:\n              ",
        "properties:\n              ",
        "items: "
      )
    )
  }

  test("Header common names") {
    this.runSuggestionTest(
      "structure/test165.raml",
      CommonHeaderNames.names.map(n => n + ":\n            ").toSet
    )
  }

  test("Library completion") {
    this.runSuggestionTest(
      "structure/test159.raml",
      Set("usage: ",
          "uses:\n  ",
          "schemas:\n  ",
          "traits:\n  ",
          "types:\n  ",
          "annotationTypes:\n  ",
          "resourceTypes:\n  ",
          "securitySchemes:\n  ")
    )
  }

  test("response codes test 01") {
    this.runSuggestionTest("structure/test160.raml", TestRamlResponseCodes.all.toSet)
  }

  test("response codes test 02") {
    this.runSuggestionTest("structure/test161.raml", TestRamlResponseCodes.all.toSet)
  }

  test("facets test 1") {
    this.runSuggestionTest("facets/test01.raml", Set("testFacet1", "testFacet2", "testFacet3", "testFacet5"))
  }

  test("facets test 2") {
    this.runSuggestionTest("facets/test02.raml",
                           Set("testFacet1: ", "testFacet2: ", "testFacet3:\n      ", "testFacet5: "))
  }

  test("methods test 1") {
    this.runSuggestionTest(
      "methods/test01.raml",
      Set("displayName",
          "type",
          "description",
          "get",
          "put",
          "post",
          "delete",
          "options",
          "head",
          "patch",
          "trace",
          "connect",
          "securedBy",
          "is",
          "uriParameters")
    )
  }

  test("methods test 2") {
    this.runSuggestionTest(
      "methods/test02.raml",
      Set(
        "displayName: ",
        "type: ",
        "description: ",
        "get:\n    ",
        "put:\n    ",
        "post:\n    ",
        "delete:\n    ",
        "options:\n    ",
        "head:\n    ",
        "patch:\n    ",
        "trace:\n    ",
        "connect:\n    ",
        "securedBy: ",
        "is:\n    ",
        "uriParameters:\n    "
      )
    )
  }

  test("methods test 3") {
    this.runSuggestionTest(
      "methods/test03.raml",
      Set("displayName",
          "type",
          "description",
          "get",
          "put",
          "post",
          "delete",
          "options",
          "head",
          "patch",
          "trace",
          "connect",
          "securedBy",
          "is",
          "uriParameters")
    )
  }

  test("test property name suggestion") {
    this.runSuggestionTest("structure/test162.raml", Set())
  }

  test("test all body content media type values") {
    this.runSuggestionTest(
      "structure/all/bodyvalue.raml",
      Set(
        "displayName: ",
        "example:\n               ",
        "type: ",
        "properties:\n               ",
        "enum: ",
        "xml:\n               ",
        "schema: ",
        "default: ",
        "examples:\n               ",
        "description: ",
        "facets:\n               ",
        "items: "
      )
    )
  }

  test("test suggestions after typed includes with double reference") {
    this.runSuggestionTest(
      "includes/double-reference/api.raml",
      Set("get:\n    ")
    )
  }

  test("test for index out of bounds") {
    this.runSuggestionTest(
      "canda-commons-api/reduced.raml",
      Set(
        "traits:\n  ",
        "documentation:\n  ",
        "baseUri: ",
        "description: ",
        "schemas:\n  ",
        "annotationTypes:\n  ",
        "resourceTypes:\n  ",
        "protocols: ",
        "types:\n  ",
        "baseUriParameters:\n  ",
        "securedBy: ",
        "mediaType: ",
        "securitySchemes:\n  "
      )
    )
  }
}
