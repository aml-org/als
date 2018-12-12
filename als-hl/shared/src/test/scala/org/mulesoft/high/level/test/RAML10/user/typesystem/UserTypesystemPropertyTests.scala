package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class UserTypesystemPropertyTests extends RAML10TypesystemTest {

  test("Local type property existence") {
    runTest("property/test001/api.raml", project => {

      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
      if (typeNode.localType.isDefined)
        succeed
      else
        fail("Local type property is not defined for the node")
    })
  }

//  test("Local type property componentType") {
//    runTest("property/test003.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.array.get.componentType.get.nameId.get == "items")
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property options") {
//    runTest("property/test004.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var unionSuperType = typeNode.localType.flatMap(_.superTypes.find(_.isUnion)).flatMap(_.union)
//      if (unionSuperType.isDefined && unionSuperType.get.options.length == 2)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }

//  test("Local type property superTypes") {
//    runTest("property/test005.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.superTypes.length == 2)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }

//  test("Local type property subTypes") {
//    runTest("property/test006.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.subTypes.length == 1)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property allSubTypes") {
//    runTest("property/test007.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.allSubTypes.length == 2)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property allSuperTypes") {
//    runTest("property/test008.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.allSuperTypes.length
//      var expectedValue = 3
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }

//  test("Local type property properties") {
//    runTest("property/test009.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.properties.length == 2)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property facet") {
//    runTest("property/test010.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.facet("noHolidays").get.nameId.get == "noHolidays")
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property allProperties") {
//    runTest("property/test011.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      if (typeNode.localType.get.allProperties.length == 2)
//        succeed
//      else
//        fail("Local type property is not defined for the node")
//    })
//  }
//
//  test("Local type property allFacets") {
//    runTest("property/test012.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.allFacets.length
//      var expectedValue = 3
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property facets") {
//    runTest("property/test013.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.facets.length
//      var expectedValue = 1
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isAnnotationType") {
//    runTest("property/test014.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isAnnotationType
//      var expectedValue = false
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isBuiltIn") {
//    runTest("property/test015.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isBuiltIn
//      var expectedValue = false
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property requiredProperties") {
//    runTest("property/test016.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.requiredProperties.length
//      var expectedValue = 2
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property getFixedFacets") {
//    runTest("property/test017.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.getFixedFacets.size
//      var expectedValue = 1
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property fixedFacets") {
//    runTest("property/test018.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.fixedFacets.size
//      var expectedValue = 1
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property allFixedFacets") {
//    runTest("property/test019.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.allFixedFacets.size
//      var expectedValue = 1
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property fixedBuiltInFacets") {
//    runTest("property/test020.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.fixedBuiltInFacets.size
//      var expectedValue = 3
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property allFixedBuiltInFacets") {
//    runTest("property/test021.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.allFixedBuiltInFacets.size
//      var expectedValue = 3
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isTopLevel") {
//    runTest("property/test022.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isTopLevel
//      var expectedValue = true
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isUserDefined") {
//    runTest("property/test023.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isUserDefined
//      var expectedValue = true
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isValueType") {
//    runTest("property/test025.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isValueType
//      var expectedValue = false
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isArray") {
//    runTest("property/test027.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isArray
//      var expectedValue = true
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property isObject") {
//    runTest("property/test028.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.isObject
//      var expectedValue = false
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property array") {
//    runTest("property/test030.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.array.get.nameId.get
//      var expectedValue = "bb"
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property hasStructure") {
//    runTest("property/test037.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.hasStructure
//      var expectedValue = true
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property property") {
//    runTest("property/test044.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.property("aa").get.nameId.get
//      var expectedValue = "aa"
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("Local type property printDetails") {
//    runTest("property/test045.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.printDetails
//      var expectedValue = "prmn[org.mulesoft.typesystem.nominal_types.ValueType]\n  Super types:\n    string[org.mulesoft.typesystem.nominal_types.ValueType]\n      Super types:\n        scalar[org.mulesoft.typesystem.nominal_types.ValueType]\n          Super types:\n            any[org.mulesoft.typesystem.nominal_types.StructuredType]"
//      if (actualValue.trim == expectedValue.trim)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }

//  test("Local type property kind") {
//    runTest("property/test049.raml", project => {
//
//      var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("t2")).get.elements("properties").head
//      var actualValue = typeNode.localType.get.kind.length
//      var expectedValue = 1
//      if (expectedValue == actualValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
}