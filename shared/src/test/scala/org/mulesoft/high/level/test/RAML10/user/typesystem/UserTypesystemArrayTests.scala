package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class UserTypesystemArrayTests extends RAML10TypesystemTest {

  test("Local array type existence") {
    runTest("array/test001/api.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.isDefined)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

//  test("Local array type componentType") {
//    runTest("array/test003.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
//      if (typeNode.localType.get.array.get.componentType.get.nameId.get == "items")
//        succeed
//      else
//        fail("Local array type is not defined for the node")
//    })
//  }

  test("Local array type options") {
    runTest("array/test004.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var unionSuperType = typeNode.localType.flatMap(_.superTypes.find(_.isUnion)).flatMap(_.union)
      if (unionSuperType.isDefined && unionSuperType.get.options.length == 2)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type superTypes") {
    runTest("array/test005.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.superTypes.length == 1)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type subTypes") {
    runTest("array/test006.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.subTypes.length == 0)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type allSubTypes") {
    runTest("array/test007.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.allSubTypes.length == 0)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type allSuperTypes") {
    runTest("array/test008.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.allSuperTypes.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type properties") {
    runTest("array/test009.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.properties.length == 2)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type facet") {
    runTest("array/test010.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.facet("noHolidays").get.nameId.get == "noHolidays")
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type allProperties") {
    runTest("array/test011.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      if (typeNode.localType.get.allProperties.length == 2)
        succeed
      else
        fail("Local array type is not defined for the node")
    })
  }

  test("Local array type allFacets") {
    runTest("array/test012.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.allFacets.length
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type facets") {
    runTest("array/test013.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.facets.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isAnnotationType") {
    runTest("array/test014.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isAnnotationType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isBuiltIn") {
    runTest("array/test015.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isBuiltIn
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type requiredProperties") {
    runTest("array/test016.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.requiredProperties.length
      var expectedValue = 2
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type getFixedFacets") {
    runTest("array/test017.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.getFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type fixedFacets") {
    runTest("array/test018.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.fixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type allFixedFacets") {
    runTest("array/test019.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.allFixedFacets.size
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type fixedBuiltInFacets") {
    runTest("array/test020.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.fixedBuiltInFacets.size
      var expectedValue = 2
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type allFixedBuiltInFacets") {
    runTest("array/test021.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.allFixedBuiltInFacets.size
      var expectedValue = 3
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isTopLevel") {
    runTest("array/test022.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isTopLevel
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isUserDefined") {
    runTest("array/test023.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isUserDefined
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isValueType") {
    runTest("array/test025.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isValueType
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isArray") {
    runTest("array/test027.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isArray
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type isObject") {
    runTest("array/test028.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.isObject
      var expectedValue = false
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

//  test("Local array type array") {
//    runTest("array/test030.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
//      var actualValue = typeNode.localType.get.array.get.nameId.get
//      var expectedValue = "bb"
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }

  test("Local array type hasStructure") {
    runTest("array/test037.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.hasStructure
      var expectedValue = true
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type property") {
    runTest("array/test044.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.property("aa").get.nameId.get
      var expectedValue = "aa"
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type printDetails") {
    runTest("array/test045.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.printDetails
      var expectedValue = "items[org.mulesoft.typesystem.nominal_types.StructuredType]\n  Super types:\n    prmn[org.mulesoft.typesystem.nominal_types.ValueType]\n      Super types:\n        string[org.mulesoft.typesystem.nominal_types.ValueType]\n          Super types:\n            scalar[org.mulesoft.typesystem.nominal_types.ValueType]\n              Super types:\n                any[org.mulesoft.typesystem.nominal_types.StructuredType]"
      if (actualValue.trim == expectedValue.trim)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Local array type kind") {
    runTest("array/test049.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("tt")).get.elements("items").head
      var actualValue = typeNode.localType.get.kind.length
      var expectedValue = 1
      if (expectedValue == actualValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}