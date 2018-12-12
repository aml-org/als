package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class UserTypesystemBodyTests extends RAML10TypesystemTest {

  test("Local type body existence") {
    runTest("body/test001/api.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.isDefined)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body componentType") {
    runTest("body/test003.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.array.get.componentType.get.nameId.get == "items")
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body options") {
    runTest("body/test004.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var unionSuperType = typeNode.localType.flatMap(_.superTypes.find(_.isUnion)).flatMap(_.union)
      if (unionSuperType.isDefined && unionSuperType.get.options.length == 2)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body superTypes") {
    runTest("body/test005.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.superTypes.length == 1)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body subTypes") {
    runTest("body/test006.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.subTypes.length == 0)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body allSubTypes") {
    runTest("body/test007.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.allSubTypes.length == 0)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body allSuperTypes") {
    runTest("body/test008.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.allSuperTypes.length
      var expectedValue = 4
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body properties") {
    runTest("body/test009.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.properties.length == 2)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body facet") {
    runTest("body/test010.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.facet("noHolidays").get.nameId.get == "noHolidays")
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body allProperties") {
    runTest("body/test011.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      if (typeNode.localType.get.allProperties.length == 2)
        succeed
      else
        fail("Local type body is not defined for the node")
    })
  }

  test("Local type body allFacets") {
    runTest("body/test012.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.allFacets.length
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body facets") {
    runTest("body/test013.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.facets.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isAnnotationType") {
    runTest("body/test014.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isAnnotationType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isBuiltIn") {
    runTest("body/test015.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isBuiltIn
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body requiredProperties") {
    runTest("body/test016.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.requiredProperties.length
      var expectedValue = 2
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body getFixedFacets") {
    runTest("body/test017.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.getFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body fixedFacets") {
    runTest("body/test018.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.fixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body allFixedFacets") {
    runTest("body/test019.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.allFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body fixedBuiltInFacets") {
    runTest("body/test020.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.fixedBuiltInFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body allFixedBuiltInFacets") {
    runTest("body/test021.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.allFixedBuiltInFacets.size
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isTopLevel") {
    runTest("body/test022.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isTopLevel
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isUserDefined") {
    runTest("body/test023.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isUserDefined
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isValueType") {
    runTest("body/test025.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isValueType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isArray") {
    runTest("body/test027.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isArray
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body isObject") {
    runTest("body/test028.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.isObject
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body array") {
    runTest("body/test030.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.array.get.nameId.get
      var expectedValue = "schema"
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body hasStructure") {
    runTest("body/test037.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.hasStructure
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body property") {
    runTest("body/test044.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.property("aa").get.nameId.get
      var expectedValue = "aa"
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body printDetails") {
    runTest("body/test045.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.printDetails
      var expectedValue = "schema[org.mulesoft.typesystem.nominal_types.ValueType]\n  Super types:\n    prmn[org.mulesoft.typesystem.nominal_types.ValueType]\n      Super types:\n        string[org.mulesoft.typesystem.nominal_types.ValueType]\n          Super types:\n            scalar[org.mulesoft.typesystem.nominal_types.ValueType]\n              Super types:\n                any[org.mulesoft.typesystem.nominal_types.StructuredType]"
      if (actualValue.trim == expectedValue.trim)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local type body kind") {
    runTest("body/test049.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
      var actualValue = typeNode.localType.get.kind.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
