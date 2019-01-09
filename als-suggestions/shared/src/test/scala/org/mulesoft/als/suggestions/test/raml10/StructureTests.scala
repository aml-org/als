package org.mulesoft.als.suggestions.test.raml10

import org.mulesoft.als.suggestions.plugins.raml.{CommonHeaderNames, CommonHeadersNamesCompletionPlugin}

class StructureTests extends RAML10Test {

  test("StructureTests responses") {
    this.runTest("structure/test01.raml", Set("responses:\n      "))
  }

  test("StructureTests root node types") {
    this.runTest("structure/test02.raml", Set("types:\n  "))
  }

  test("StructureTests root node resourceTypes") {
    this.runTest("structure/test03.raml", Set("resourceTypes"))
  }

  test("StructureTests root node title") {
    this.runTest("structure/test04.raml", Set("title:"))
  }

  test("StructureTests root node traits") {
    this.runTest("structure/test05.raml", Set("traits:\n  "))
  }

  test("StructureTests root node description") {
    this.runTest("structure/test06.raml", Set("description:"))
  }

  test("StructureTests root node documentation") {
    this.runTest("structure/test07.raml", Set("documentation:\n  "))
  }

  test("StructureTests root node version") {
    this.runTest("structure/test08.raml", Set("version:"))
  }

  test("StructureTests root node baseUri") {
    this.runTest("structure/test09.raml", Set("baseUri:", "baseUriParameters:\n  "))
  }

  test("StructureTests root node protocols") {
    this.runTest("structure/test10.raml", Set("protocols:"))
  }

  test("StructureTests root node mediaType") {
    this.runTest("structure/test11.raml", Set("mediaType:"))
  }

  test("StructureTests root node schemas") {
    this.runTest("structure/test12.raml", Set("schemas:\n  "))
  }

  test("StructureTests root node securitySchemes") {
    this.runTest("structure/test13.raml", Set("securitySchemes:\n  "))
  }

  test("StructureTests root node securedBy") {
    this.runTest("structure/test14.raml", Set("securedBy:"))
  }

  test("StructureTests root node annotationTypes") {
    this.runTest("structure/test15.raml", Set("annotationTypes:\n  "))
  }

  test("StructureTests root node uses") {
    this.runTest("structure/test16.raml", Set("uses:\n  "))
  }

  test("StructureTests node method body") {
    this.runTest("structure/test18.raml", Set("body:\n      "))
  }

  test("StructureTests resource node is") {
    this.runTest("structure/test21.raml", Set("is:\n    "))
  }

  test("StructureTests resource node type") {
    this.runTest("structure/test22.raml", Set("type:"))
  }

  test("StructureTests resource node description") {
    this.runTest("structure/test23.raml", Set("description:"))
  }

  test("StructureTests resource node displayName") {
    this.runTest("structure/test24.raml", Set("displayName:"))
  }

  test("StructureTests resource node put, post, patch") {
    this.runTest("structure/test26.raml", Set("put:\n    ", "post:\n    ", "patch:\n    "))
  }

  test("StructureTests resource node uriParameters") {
    this.runTest("structure/test27.raml", Set("uriParameters:\n    "))
  }

  test("StructureTests resource node options") {
    this.runTest("structure/test28.raml", Set("options:\n    "))
  }

  test("StructureTests resource node head") {
    this.runTest("structure/test29.raml", Set("head:\n    "))
  }

  test("StructureTests resource node trace") {
    this.runTest("structure/test30.raml", Set("trace:\n    "))
  }

  test("StructureTests resource node connect") {
    this.runTest("structure/test31.raml", Set("connect:\n    "))
  }

  test("StructureTests method node queryString, queryParameters") {
    this.runTest("structure/test32.raml", Set("queryString:\n      ", "queryParameters:\n      "))
  }

  test("StructureTests method node headers") {
    this.runTest("structure/test33.raml", Set("headers:\n      "))
  }

  test("StructureTests method node response description") {
    this.runTest("structure/test34.raml", Set("description:"))
  }

  test("StructureTests method node responses") {
    this.runTest("structure/test35.raml", Set("responses:\n      "))
  }

  test("StructureTests method node is") {
    this.runTest("structure/test37.raml", Set("is:\n      "))
  }

  test("StructureTests method node description") {
    this.runTest("structure/test38.raml", Set("description:"))
  }

  test("StructureTests method node displayName") {
    this.runTest("structure/test39.raml", Set("displayName:"))
  }

  test("StructureTests method node response headers") {
    this.runTest("structure/test41.raml", Set("headers:\n          "))
  }

  test("StructureTests method node response body") {
    this.runTest("structure/test42.raml", Set("body:\n          "))
  }

  test("StructureTests resourceType node is") {
    this.runTest("structure/test44.raml", Set("is:\n      "))
  }

  test("StructureTests resourceType node type") {
    this.runTest("structure/test45.raml", Set("type:"))
  }

  test("StructureTests resourceType node description") {
    this.runTest("structure/test46.raml", Set("description:"))
  }

  test("StructureTests resourceType node displayName") {
    this.runTest("structure/test47.raml", Set("displayName:"))
  }

  test("StructureTests resourceType operation nodes suggestions") {
    this.runTest("structure/test163.raml", Set("responses"))
  }

  test("StructureTests resourceType in fragment operation nodes suggestions") {
    this.runTest("structure/test163.raml", Set("responses"))
  }

  test("StructureTests resourceType node put, post, patch") {
    this.runTest("structure/test49.raml", Set("put:\n      ", "post:\n      ", "patch:\n      "))
  }

  test("StructureTests resourceType node uriParameters") {
    this.runTest("structure/test50.raml", Set("uriParameters:\n      "))
  }

  test("StructureTests resourceType node options") {
    this.runTest("structure/test51.raml", Set("options:\n      "))
  }

  test("StructureTests resourceType node head") {
    this.runTest("structure/test52.raml", Set("head:\n      "))
  }

  test("StructureTests resourceType node trace") {
    this.runTest("structure/test53.raml", Set("trace:\n      "))
  }

  test("StructureTests resourceType node connect") {
    this.runTest("structure/test54.raml", Set("connect:\n      "))
  }

  test("StructureTests resourceType node usage") {
    this.runTest("structure/test55.raml", Set("usage:"))
  }

  test("StructureTests trait node queryString, queryParameters") {
    this.runTest("structure/test57.raml", Set("queryString:\n      ", "queryParameters:\n      "))
  }

  test("StructureTests trait node headers") {
    this.runTest("structure/test58.raml", Set("headers:\n      "))
  }

  test("StructureTests trait node responses") {
    this.runTest("structure/test60.raml", Set("responses:\n      "))
  }

  test("StructureTests trait node description") {
    this.runTest("structure/test63.raml", Set("description:"))
  }

  test("StructureTests trait node displayName") {
    this.runTest("structure/test64.raml", Set("displayName:"))
  }

  test("StructureTests trait node body") {
    this.runTest("structure/test69.raml", Set("body:\n      "))
  }

  test("StructureTests Security Scheme Declaration node type") {
    this.runTest("structure/test83.raml", Set("type:"))
  }

  test("StructureTests Security Scheme Declaration node settings ") {
    this.runTest("structure/test84.raml", Set("settings:\n      "))
  }

  test("StructureTests Security Scheme Declaration node OAuth 2.0 settings accessTokenUri") {
    this.runTest("structure/test86.raml", Set("accessTokenUri:"))
  }

  test("StructureTests Security Scheme Declaration node description") {
    this.runTest("structure/test89.raml", Set("description:"))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node headers") {
    this.runTest("structure/test91.raml", Set("headers:\n        "))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node responses") {
    this.runTest("structure/test93.raml", Set("responses:\n        "))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node requestTokenUri") {
    this.runTest("structure/test94.raml", Set("requestTokenUri:"))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node tokenCredentialsUri") {
    this.runTest("structure/test96.raml", Set("tokenCredentialsUri:"))
  }

  test("StructureTests Security Scheme Declaration node displayName") {
    this.runTest("structure/test98.raml", Set("displayName:"))
  }

  test("Declaring Annotation Type facet 'allowedTargets'") {
    this.runTest("structure/test99.raml", Set("allowedTargets:\n      "))
  }

  test("Declaring Annotation Type facet minLength completion") {
    this.runTest("structure/test100.raml", Set("minLength:"))
  }

  test("Declaring Annotation Type facet maxLength completion") {
    this.runTest("structure/test101.raml", Set("maxLength:"))
  }

  test("Declaring Annotation Type facet example, examples completion") {
    this.runTest("structure/test102.raml", Set("example:\n      ", "examples:\n      "))
  }

  test("Declaring Annotation Type facet default completion") {
    this.runTest("structure/test104.raml", Set("default:"))
  }

  test("Declaring Annotation Type facet displayName completion") {
    this.runTest("structure/test105.raml", Set("displayName:"))
  }

  test("Declaring Annotation Type facet description completion") {
    this.runTest("structure/test106.raml", Set("description:"))
  }

  test("Declaring Annotation Type facet required completion") {
    this.runTest("structure/test107.raml", Set())
  }

  test("Declaring Annotation Type facet repeat completion") {
    this.runTest("structure/test108.raml", Set())
  }

  test("Declaring Annotation Type facet pattern completion") {
    this.runTest("structure/test109.raml", Set("pattern:"))
  }

  test("Declaring Annotation Type facet type completion") {
    this.runTest("structure/test110.raml", Set("type:"))
  }

  test("Declaring Annotation Type facet properties completion") {
    this.runTest("structure/test111.raml", Set("properties:\n      "))
  }

  test("Declaring Annotation Type facet schema completion") {
    this.runTest("structure/test112.raml", Set("schema:"))
  }

  test("Declaring Annotation Type facet facets completion") {
    this.runTest("structure/test113.raml", Set("facets:\n      "))
  }

  test("Declaring Annotation Type facet xml completion") {
    this.runTest("structure/test114.raml", Set("xml:\n      "))
  }

  test("Type facet minLength completion") {
    this.runTest("structure/test115.raml", Set("minLength:"))
  }

  test("Type facet maxLength completion") {
    this.runTest("structure/test116.raml", Set("maxLength:"))
  }

  test("Type facet example, examples completion") {
    this.runTest("structure/test117.raml", Set("example:\n      ", "examples:\n      "))
  }

  test("Type facet default completion") {
    this.runTest("structure/test119.raml", Set("default:"))
  }

  test("Type facet displayName completion") {
    this.runTest("structure/test120.raml", Set("displayName:"))
  }

  test("Type facet description completion") {
    this.runTest("structure/test121.raml", Set("description:"))
  }

  test("Type facet required completion") {
    this.runTest("structure/test122.raml", Set())
  }

  test("Type facet repeat completion") {
    this.runTest("structure/test123.raml", Set())
  }

  test("Type facet pattern completion") {
    this.runTest("structure/test124.raml", Set("pattern:"))
  }

  test("Type facet type completion") {
    this.runTest("structure/test125.raml", Set("type:"))
  }

  test("Type facet schema completion") {
    this.runTest("structure/test127.raml", Set("schema:"))
  }

  test("Type facet facets completion") {
    this.runTest("structure/test128.raml", Set("facets:\n      "))
  }

  test("Type facet xml completion") {
    this.runTest("structure/test129.raml", Set("xml:\n      "))
  }

  test("Parameter facet minLength completion") {
    this.runTest("structure/test130.raml", Set("minLength:"))
  }

  test("Parameter facet maxLength completion") {
    this.runTest("structure/test131.raml", Set("maxLength:"))
  }

  test("Parameter facet example, examples completion") {
    this.runTest("structure/test132.raml", Set("example:\n          ", "examples:\n          "))
  }

  test("Parameter facet default completion") {
    this.runTest("structure/test134.raml", Set("default:"))
  }

  test("Parameter facet displayName completion") {
    this.runTest("structure/test135.raml", Set("displayName:"))
  }

  test("Parameter facet description completion") {
    this.runTest("structure/test136.raml", Set("description:"))
  }

  test("Parameter facet repeat completion") {
    this.runTest("structure/test138.raml", Set())
  }

  test("Parameter facet pattern completion") {
    this.runTest("structure/test139.raml", Set("pattern:"))
  }

  test("Parameter facet type completion") {
    this.runTest("structure/test140.raml", Set("type:"))
  }

  test("Parameter facet schema completion") {
    this.runTest("structure/test141.raml", Set("schema:"))
  }

  test("Parameter facet facets completion") {
    this.runTest("structure/test142.raml", Set("facets:\n          "))
  }

  test("Parameter facet xml completion") {
    this.runTest("structure/test143.raml", Set("xml:\n          "))
  }

  test("discriminator") {
    this.runTest("structure/test145.raml", Set("one", "two"))
  }

  test("documentation item fields") {
    this.runTest("structure/test146.raml", Set("title:", "content:"))
  }

  test("documentation item 'title'") {
    this.runTest("structure/test147.raml", Set("title:"))
  }

  test("documentation item 'content'") {
    this.runTest("structure/test148.raml", Set("content:"))
  }

  test("documentation item 'content' after 'title'") {
    this.runTest("structure/test149.raml", Set("content:"))
  }

  test("documentation item 'title' before 'content'") {
    this.runTest("structure/test150.raml", Set("title:"))
  }

  test("cursor on single value property") {
    this.runTest(
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
    this.runTest(
      "structure/test152.raml",
      Set(
        "enum:",
        "displayName:",
        "additionalProperties:",
        "xml:\n          ",
        "discriminator:",
        "default:",
        "maxProperties:",
        "minProperties:",
        "description:",
        "discriminatorValue:",
        "examples:\n          ",
        "facets:\n          ",
        "example:\n          "
      )
    )
  }

  test("Type property 'required' completion") {
    this.runTest("structure/test153.raml", Set("required:"))
  }

  test("Query parameter with comment") {
    this.runTest(
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
          "description")
    )
  }

  test("StructureTests schema 1") {
    this.runTest(
      "structure/test155.raml",
      Set(
        "enum:",
        "displayName:",
        "xml:\n        ",
        "default:",
        "pattern:",
        "description:",
        "maxLength:",
        "minLength:",
        "examples:\n        ",
        "facets:\n        ",
        "example:\n        ",
        "properties:\n        "
      )
    )
  }

  test("StructureTests schema 2") {
    this.runTest(
      "structure/test156.raml",
      Set(
        "application/json:\n        ",
        "application/xml:\n        ",
        "multipart/form-data:\n        ",
        "application/x-www-form-urlencoded:\n        ",
        "displayName:",
        "type:",
        "enum:",
        "xml:\n        ",
        "default:",
        "description:",
        "schema:",
        "examples:\n        ",
        "example:\n        ",
        "facets:\n        ",
        "properties:\n        "
      )
    )
  }

  test("Base URI parameter completion") {
    this.runTest(
      "structure/test157.raml",
      Set(
        "enum:",
        "displayName:",
        "type:",
        "xml:\n      ",
        "default:",
        "pattern:",
        "description:",
        "maxLength:",
        "minLength:",
        "examples:\n      ",
        "schema:",
        "facets:\n      ",
        "example:\n      ",
        "properties:\n      "
      )
    )
  }

  test("Header completion") {
    this.runTest(
      "structure/test158.raml",
      Set(
        "required:",
        "enum:",
        "displayName:",
        "type:",
        "xml:\n              ",
        "default:",
        "pattern:",
        "description:",
        "maxLength:",
        "minLength:",
        "examples:\n              ",
        "schema:",
        "facets:\n              ",
        "example:\n              ",
        "properties:\n              "
      )
    )
  }

  test("Header common names") {
    this.runTest(
      "structure/test165.raml",
      CommonHeaderNames.names.map(n => n + ":\n            ").toSet
    )
  }

  test("Library completion") {
    this.runTest(
      "structure/test159.raml",
      Set("usage:",
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
    this.runTest("structure/test160.raml", TestRamlResponseCodes.all.toSet)
  }

  test("response codes test 02") {
    this.runTest("structure/test161.raml", TestRamlResponseCodes.all.toSet)
  }

  test("facets test 1") {
    this.runTest("facets/test01.raml", Set("testFacet1", "testFacet2", "testFacet3", "testFacet5"))
  }

  test("facets test 2") {
    this.runTest("facets/test02.raml", Set("testFacet1:", "testFacet2:", "testFacet3:\n      ", "testFacet5:"))
  }

  test("methods test 1") {
    this.runTest(
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
    this.runTest(
      "methods/test02.raml",
      Set(
        "displayName:",
        "type:",
        "description:",
        "get:\n    ",
        "put:\n    ",
        "post:\n    ",
        "delete:\n    ",
        "options:\n    ",
        "head:\n    ",
        "patch:\n    ",
        "trace:\n    ",
        "connect:\n    ",
        "securedBy:",
        "is:\n    ",
        "uriParameters:\n    "
      )
    )
  }

  test("methods test 3") {
    this.runTest(
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
    this.runTest("structure/test162.raml", Set())
  }

  test("test all body content media type values") {
    this.runTest(
      "structure/all/bodyvalue.raml",
      Set(
        "displayName:",
        "example:\n               ",
        "type:",
        "properties:\n               ",
        "enum:",
        "xml:\n               ",
        "schema:",
        "default:",
        "examples:\n               ",
        "description:",
        "facets:\n               "
      )
    )
  }
}
