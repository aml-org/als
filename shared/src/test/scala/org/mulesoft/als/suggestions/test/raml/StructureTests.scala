package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class StructureTests extends RAMLTest {

  test("StructureTests responses"){
    this.runTest("structure/test01.raml", Set("responses"))
  }

  test("StructureTests root node types"){
    this.runTest("structure/test02.raml", Set("types"))
  }

  test("StructureTests root node title"){
    this.runTest("structure/test04.raml", Set("title"))
  }

  test("StructureTests root node traits"){
    this.runTest("structure/test05.raml", Set("traits"))
  }

  test("StructureTests root node description"){
    this.runTest("structure/test06.raml", Set("description"))
  }

  test("StructureTests root node version"){
    this.runTest("structure/test08.raml", Set("version"))
  }

  test("StructureTests root node schemas"){
    this.runTest("structure/test12.raml", Set("schemas"))
  }

  test("StructureTests root node securitySchemes"){
    this.runTest("structure/test13.raml", Set("securitySchemes"))
  }

  test("StructureTests root node annotationTypes"){
    this.runTest("structure/test15.raml", Set("annotationTypes"))
  }

  test("StructureTests root node uses"){
    this.runTest("structure/test16.raml", Set("uses"))
  }

  test("StructureTests node method body"){
    this.runTest("structure/test18.raml", Set("body"))
  }

  test("StructureTests resource node type"){
    this.runTest("structure/test22.raml", Set("type"))
  }

  test("StructureTests resource node description"){
    this.runTest("structure/test23.raml", Set("description"))
  }

  test("StructureTests resource node displayName"){
    this.runTest("structure/test24.raml", Set("displayName"))
  }

  test("StructureTests method node queryString, queryParameters"){
    this.runTest("structure/test32.raml", Set("queryString", "queryParameters"))
  }

  test("StructureTests method node headers"){
    this.runTest("structure/test33.raml", Set("headers"))
  }

  test("StructureTests method node response description"){
    this.runTest("structure/test34.raml", Set("description"))
  }

  test("StructureTests method node responses"){
    this.runTest("structure/test35.raml", Set("responses"))
  }

  test("StructureTests method node description"){
    this.runTest("structure/test38.raml", Set("description"))
  }

  test("StructureTests method node displayName"){
    this.runTest("structure/test39.raml", Set("displayName"))
  }

  test("StructureTests method node response headers"){
    this.runTest("structure/test41.raml", Set("headers"))
  }

  test("StructureTests method node response body"){
    this.runTest("structure/test42.raml", Set("body"))
  }

  test("StructureTests resourceType node type"){
    this.runTest("structure/test45.raml", Set("type"))
  }

  test("StructureTests resourceType node description"){
    this.runTest("structure/test46.raml", Set("description"))
  }

  test("StructureTests resourceType node displayName"){
    this.runTest("structure/test47.raml", Set("displayName"))
  }

  test("StructureTests resourceType node usage"){
    this.runTest("structure/test55.raml", Set("usage"))
  }

  test("StructureTests trait node queryString, queryParameters"){
    this.runTest("structure/test57.raml", Set("queryString", "queryParameters"))
  }

  test("StructureTests trait node headers"){
    this.runTest("structure/test58.raml", Set("headers"))
  }

  test("StructureTests trait node responses"){
    this.runTest("structure/test60.raml", Set("responses"))
  }

  test("StructureTests trait node description"){
    this.runTest("structure/test63.raml", Set("description"))
  }

  test("StructureTests trait node displayName"){
    this.runTest("structure/test64.raml", Set("displayName"))
  }

  test("StructureTests trait node body"){
    this.runTest("structure/test69.raml", Set("body"))
  }

  test("StructureTests resourceType method node response description"){
    this.runTest("structure/test73.raml", Set("description"))
  }

  test("StructureTests resourceType method node description"){
    this.runTest("structure/test77.raml", Set("description"))
  }

  test("StructureTests resourceType method node displayName"){
    this.runTest("structure/test78.raml", Set("displayName"))
  }

  test("StructureTests Security Scheme Declaration node type"){
    this.runTest("structure/test83.raml", Set("type"))
  }

  test("StructureTests Security Scheme Declaration node settings "){
    this.runTest("structure/test84.raml", Set("settings"))
  }

  test("StructureTests Security Scheme Declaration node OAuth 2.0 settings accessTokenUri"){
    this.runTest("structure/test86.raml", Set("accessTokenUri"))
  }

  test("StructureTests Security Scheme Declaration node description"){
    this.runTest("structure/test89.raml", Set("description"))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node headers"){
    this.runTest("structure/test91.raml", Set("headers"))
  }

  test("StructureTests Security Scheme Declaration OAuth 2.0 describedBy node responses"){
    this.runTest("structure/test93.raml", Set("responses"))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node requestTokenUri"){
    this.runTest("structure/test94.raml", Set("requestTokenUri"))
  }

  test("StructureTests Security Scheme Declaration OAuth 1.0 settings node tokenCredentialsUri"){
    this.runTest("structure/test96.raml", Set("tokenCredentialsUri"))
  }

  test("StructureTests Security Scheme Declaration node displayName"){
    this.runTest("structure/test98.raml", Set("displayName"))
  }

  test("Declaring Annotation Type facet minLength completion"){
    this.runTest("structure/test100.raml", Set("minLength"))
  }

  test("Declaring Annotation Type facet maxLength completion"){
    this.runTest("structure/test101.raml", Set("maxLength"))
  }

  test("Declaring Annotation Type facet example, examples completion"){
    this.runTest("structure/test102.raml", Set("example", "examples"))
  }

  test("Declaring Annotation Type facet default completion"){
    this.runTest("structure/test104.raml", Set("default"))
  }

  test("Declaring Annotation Type facet displayName completion"){
    this.runTest("structure/test105.raml", Set("displayName"))
  }

  test("Declaring Annotation Type facet description completion"){
    this.runTest("structure/test106.raml", Set("description"))
  }

  test("Declaring Annotation Type facet pattern completion"){
    this.runTest("structure/test109.raml", Set("pattern"))
  }

  test("Declaring Annotation Type facet type completion"){
    this.runTest("structure/test110.raml", Set("type"))
  }

  test("Declaring Annotation Type facet schema completion"){
    this.runTest("structure/test112.raml", Set("schema"))
  }

  test("Declaring Annotation Type facet facets completion"){
    this.runTest("structure/test113.raml", Set("facets"))
  }

  test("Declaring Annotation Type facet xml completion"){
    this.runTest("structure/test114.raml", Set("xml"))
  }

  test("Type facet minLength completion"){
    this.runTest("structure/test115.raml", Set("minLength"))
  }

  test("Type facet maxLength completion"){
    this.runTest("structure/test116.raml", Set("maxLength"))
  }

  test("Type facet example, examples completion"){
    this.runTest("structure/test117.raml", Set("example","examples"))
  }

  test("Type facet default completion"){
    this.runTest("structure/test119.raml", Set("default"))
  }

  test("Type facet displayName completion"){
    this.runTest("structure/test120.raml", Set("displayName"))
  }

  test("Type facet description completion"){
    this.runTest("structure/test121.raml", Set("description"))
  }

  test("Type facet pattern completion"){
    this.runTest("structure/test124.raml", Set("pattern"))
  }

  test("Type facet type completion"){
    this.runTest("structure/test125.raml", Set("type"))
  }

  test("Type facet schema completion"){
    this.runTest("structure/test127.raml", Set("schema"))
  }

  test("Type facet facets completion"){
    this.runTest("structure/test128.raml", Set("facets"))
  }

  test("Type facet xml completion"){
    this.runTest("structure/test129.raml", Set("xml"))
  }

  test("Parameter facet minLength completion"){
    this.runTest("structure/test130.raml", Set("minLength"))
  }

  test("Parameter facet maxLength completion"){
    this.runTest("structure/test131.raml", Set("maxLength"))
  }

  test("Parameter facet example, examples completion"){
    this.runTest("structure/test132.raml", Set("example", "examples"))
  }

  test("Parameter facet default completion"){
    this.runTest("structure/test134.raml", Set("default"))
  }

  test("Parameter facet displayName completion"){
    this.runTest("structure/test135.raml", Set("displayName"))
  }

  test("Parameter facet description completion"){
    this.runTest("structure/test136.raml", Set("description"))
  }

  test("Parameter facet pattern completion"){
    this.runTest("structure/test139.raml", Set("pattern"))
  }

  test("Parameter facet type completion"){
    this.runTest("structure/test140.raml", Set("type"))
  }

  test("Parameter facet schema completion"){
    this.runTest("structure/test141.raml", Set("schema"))
  }

  test("Parameter facet facets completion"){
    this.runTest("structure/test142.raml", Set("facets"))
  }

  test("Parameter facet xml completion"){
    this.runTest("structure/test143.raml", Set("xml"))
  }
}
