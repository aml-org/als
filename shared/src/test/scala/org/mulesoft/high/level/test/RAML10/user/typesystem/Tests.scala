package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class Tests extends RAML10TypesystemTest {

    test("Local type existence") {
        runTest("test001/api.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").head
            if (typeNode.localType.isDefined)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type componentType") {
        runTest("test003.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").head
            if (typeNode.localType.get.array.get.componentType.get.nameId.get == "items")
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type superTypes") {
        runTest("test005.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("arr")).get
            if (typeNode.localType.get.superTypes.length == 2)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type allSubTypes") {
        runTest("test007.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("CustomDate")).get
            if (typeNode.localType.get.allSubTypes.length == 2)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type properties") {
        runTest("test009.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            if (typeNode.localType.get.properties.length == 2)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type allProperties") {
        runTest("test011.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("arr")).get
            if (typeNode.localType.get.allProperties.length == 2)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }

    test("Local type allFacets") {
        runTest("test012.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.allFacets.length
            var expectedValue = 3
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type facets") {
        runTest("test013.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.facets.length
            var expectedValue = 1
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type isAnnotationType") {
        runTest("test014.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.isAnnotationType
            var expectedValue = false
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type isBuiltIn") {
        runTest("test015.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.isBuiltIn
            var expectedValue = false
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type requiredProperties") {
        runTest("test016.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            var actualValue = typeNode.localType.get.requiredProperties.length
            var expectedValue = 2
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type getFixedFacets") {
        runTest("test017.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.getFixedFacets.size
            var expectedValue = 1
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type fixedFacets") {
        runTest("test018.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.fixedFacets.size
            var expectedValue = 1
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type allFixedFacets") {
        runTest("test019.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("PossibleMeetingDate")).get
            var actualValue = typeNode.localType.get.allFixedFacets.size
            var expectedValue = 1
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type fixedBuiltInFacets") {
        runTest("test020.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            var actualValue = typeNode.localType.get.fixedBuiltInFacets.size
            var expectedValue = 3
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type allFixedBuiltInFacets") {
        runTest("test021.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            var actualValue = typeNode.localType.get.allFixedBuiltInFacets.size
            var expectedValue = 3
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type isTopLevel") {
        runTest("test022.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            var actualValue = typeNode.localType.get.isTopLevel
            var expectedValue = true
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }

    test("Local type isUserDefined") {
        runTest("test023.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").find(t=> t.attribute("name").get.value.contains("abc")).get
            var actualValue = typeNode.localType.get.isUserDefined
            var expectedValue = true
            if (expectedValue == actualValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        })
    }
}
