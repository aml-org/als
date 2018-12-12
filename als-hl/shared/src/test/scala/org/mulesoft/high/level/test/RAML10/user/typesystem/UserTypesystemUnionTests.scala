package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class UserTypesystemUnionTests extends RAML10TypesystemTest {

  test("Local type union existence") {
    runTest("union/test001/api.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("union")).get
      if (typeNode.localType.isDefined)
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

//  test("Local type union componentType") {
//    runTest("union/test003.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("union")).get
//      if (typeNode.localType.get.array.get.componentType.get.nameId.get == "items")
//        succeed
//      else
//        fail("Local type union is not defined for the node")
//    })
//  }

  test("Local type union options") {
    runTest("union/test004.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("arr")).get
      var unionSuperType = typeNode.localType.flatMap(_.superTypes.find(_.isUnion)).flatMap(_.union)
      if (unionSuperType.isDefined && unionSuperType.get.options.length == 2)
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

  test("Local type union superTypes") {
    runTest("union/test005.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("arr")).get
      if (typeNode.localType.get.superTypes.length == 2)
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

//  test("Local type union subTypes") {
//    runTest("union/test006.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("CustomDate")).get
//      if (typeNode.localType.get.subTypes.length == 1)
//        succeed
//      else
//        fail("Local type union is not defined for the node")
//    })
//  }
//
//  test("Local type union allSubTypes") {
//    runTest("union/test007.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("CustomDate")).get
//      if (typeNode.localType.get.allSubTypes.length == 2)
//        succeed
//      else
//        fail("Local type union is not defined for the node")
//    })
//  }

  test("Local type union allSuperTypes") {
    runTest("union/test008.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.allSuperTypes.length
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union properties") {
    runTest("union/test009.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      if (typeNode.localType.get.properties.length == 2)
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

  test("Local type union facet") {
    runTest("union/test010.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("CustomDate")).get
      if (typeNode.localType.get.facet("noHolidays").get.nameId.get == "noHolidays")
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

  test("Local type union allProperties") {
    runTest("union/test011.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("arr")).get
      if (typeNode.localType.get.allProperties.length == 2)
        succeed
      else
        fail("Local type union is not defined for the node")
    })
  }

  test("Local type union allFacets") {
    runTest("union/test012.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.allFacets.length
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union facets") {
    runTest("union/test013.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.facets.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isAnnotationType") {
    runTest("union/test014.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.isAnnotationType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isBuiltIn") {
    runTest("union/test015.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.isBuiltIn
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union requiredProperties") {
    runTest("union/test016.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.requiredProperties.length
      var expectedValue = 2
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union getFixedFacets") {
    runTest("union/test017.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.getFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union fixedFacets") {
    runTest("union/test018.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.fixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union allFixedFacets") {
    runTest("union/test019.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
      var actualValue = typeNode.localType.get.allFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union fixedBuiltInFacets") {
    runTest("union/test020.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.fixedBuiltInFacets.size
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union allFixedBuiltInFacets") {
    runTest("union/test021.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.allFixedBuiltInFacets.size
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isTopLevel") {
    runTest("union/test022.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.isTopLevel
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isUserDefined") {
    runTest("union/test023.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.isUserDefined
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isValueType") {
    runTest("union/test025.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.isValueType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isArray") {
    runTest("union/test027.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("bb")).get
      var actualValue = typeNode.localType.get.isArray
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union isObject") {
    runTest("union/test028.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("bb")).get
      var actualValue = typeNode.localType.get.isObject
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union array") {
    runTest("union/test030.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("bb")).get
      var actualValue = typeNode.localType.get.array.get.nameId.get
      var expectedValue = "bb"
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union hasStructure") {
    runTest("union/test037.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.hasStructure
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union property") {
    runTest("union/test044.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.property("aa").get.nameId.get
      var expectedValue = "aa"
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union printDetails") {
    runTest("union/test045.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("prmn")).get
      var actualValue = typeNode.localType.get.printDetails
      var expectedValue = "prmn[org.mulesoft.typesystem.nominal_types.ValueType]\n  Super types:\n    string[org.mulesoft.typesystem.nominal_types.ValueType]\n      Super types:\n        scalar[org.mulesoft.typesystem.nominal_types.ValueType]\n          Super types:\n            any[org.mulesoft.typesystem.nominal_types.StructuredType]"
      if (actualValue.trim == expectedValue.trim)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type union kind") {
    runTest("union/test049.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
      var actualValue = typeNode.localType.get.kind.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}