package org.mulesoft.als.suggestions.test.raml08

import org.mulesoft.als.suggestions.test.raml10.TestRamlResponseCodes

class StructureTests extends RAML08Test {

  test("StructureTests responses") {
    this.runTest("structure/test01.raml", Set("responses:\n      "))
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

  test("StructureTests method node queryParameters") {
    this.runTest("structure/test32.raml", Set("queryParameters:\n      "))
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

  test("StructureTests method node response headers") {
    this.runTest("structure/test41.raml", Set("headers:\n          "))
  }

  test("StructureTests method node response body") {
    this.runTest("structure/test42.raml", Set("body:\n          "))
  }

  test("StructureTests resourceType node is") {
    this.runTest("structure/test44.raml", Set("is:\n        "))
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

  test("StructureTests resourceType node put, post, patch") {
    this.runTest("structure/test49.raml", Set("put:\n        ", "post:\n        ", "patch:\n        "))
  }

  test("StructureTests resourceType node uriParameters") {
    this.runTest("structure/test50.raml", Set("uriParameters:\n        "))
  }

  test("StructureTests resourceType node options") {
    this.runTest("structure/test51.raml", Set("options:\n        "))
  }

  test("StructureTests resourceType node head") {
    this.runTest("structure/test52.raml", Set("head:\n        "))
  }

  test("StructureTests resourceType node trace") {
    this.runTest("structure/test53.raml", Set("trace:\n        "))
  }

  test("StructureTests resourceType node connect") {
    this.runTest("structure/test54.raml", Set("connect:\n        "))
  }

  test("StructureTests resourceType node usage") {
    this.runTest("structure/test55.raml", Set("usage:"))
  }

  test("StructureTests trait node queryString, queryParameters") {
    this.runTest("structure/test57.raml", Set("queryParameters:\n        "))
  }

  test("StructureTests trait node headers") {
    this.runTest("structure/test58.raml", Set("headers:\n        "))
  }

  test("StructureTests trait node responses") {
    this.runTest("structure/test60.raml", Set("responses:\n        "))
  }

  test("StructureTests trait node description") {
    this.runTest("structure/test63.raml", Set("description:"))
  }

  test("StructureTests trait node displayName") {
    this.runTest("structure/test64.raml", Set("displayName:"))
  }

  test("StructureTests trait node body") {
    this.runTest("structure/test69.raml", Set("body:\n        "))
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

  test("documentation item fields") {
    this.runTest("structure/test98.raml", Set("title:", "content:"))
  }

  test("documentation item 'title'") {
    this.runTest("structure/test99.raml", Set("title:"))
  }

  test("documentation item 'content'") {
    this.runTest("structure/test100.raml", Set("content:"))
  }

  test("documentation item 'content' after 'title'") {
    this.runTest("structure/test101.raml", Set("content:"))
  }

  test("documentation item 'title' before 'content'") {
    this.runTest("structure/test102.raml", Set("title:"))
  }

  test("response codes test 01") {
    this.runTest("structure/test103.raml", TestRamlResponseCodes.all)
  }

  test("response codes test 02") {
    this.runTest("structure/test104.raml", TestRamlResponseCodes.all)
  }

  test("methods test 1") {
    this.runTest(
      "methods/test01.raml",
      Set(
        "displayName",
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
        "uriParameters",
        "baseUriParameters"
      )
    );
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
        "uriParameters:\n    ",
        "baseUriParameters:\n    "
      )
    );
  }
}
