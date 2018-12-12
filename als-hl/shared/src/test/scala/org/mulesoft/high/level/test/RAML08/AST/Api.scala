package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class Api extends RAML08ASTTest{

    test("API title"){
        runTest( "ASTTests/Api/api_title.raml", project => {

            var expectedValue = "test API"
            project.rootASTUnit.rootNode.attribute("title") match {
                case Some(a) => a.value match {
                    case Some("test API") => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail("'title' attribute not found")
            }
        })
    }

    test("API version"){
        runTest( "ASTTests/Api/api_version.raml", project => {

            var expectedValue = "1.0"
            project.rootASTUnit.rootNode.attribute("version") match {
                case Some(a) => a.value match {
                    case Some("1.0") => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail("'version' attribute not found")
            }
        })
    }

    test("API resources"){
        runTest( "ASTTests/Api/api_resources.raml", project => {

            var expectedValue = 1
            var length = project.rootASTUnit.rootNode.elements("resources").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API securedBy"){
        runTest( "ASTTests/Api/api_secured_by.raml", project => {

            var expectedValue = 1
            var length = project.rootASTUnit.rootNode.elements("securedBy").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API baseUriParameters"){
        runTest( "ASTTests/Api/api_base_uri_parameters.raml", project => {

            var expectedValue = 2
            var length = project.rootASTUnit.rootNode.elements("baseUriParameters").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API protocols"){
        runTest( "ASTTests/Api/api_protocols.raml", project => {

            var expectedValue = 2
            var length = project.rootASTUnit.rootNode.attributes("protocols").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API documentation"){
        runTest( "ASTTests/Api/api_documentation.raml", project => {

            var expectedValue = 1
            var length = project.rootASTUnit.rootNode.elements("documentation").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API mediaType"){
        runTest( "ASTTests/Api/api_media_type.raml", project => {

            var expectedValue = 1
            var length = project.rootASTUnit.rootNode.attributes("mediaType").length
                if (length == expectedValue)
                    succeed
                else
                    fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API baseUri"){
        runTest( "ASTTests/Api/api_base_uri.raml", project => {

            var expectedValue = "www.{domain_name}.org"
            project.rootASTUnit.rootNode.attribute("baseUri") match {
                case Some(a) => a.value match {
                    case Some("www.{domain_name}.org") => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail("'baseUri' attribute not found")
            }
        })
    }

    test("API schemas"){
        runTest( "ASTTests/Api/api.raml", project => {

            var expectedValue = 1
            var length = project.rootASTUnit.rootNode.elements("schemas").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API traits"){
        runTest( "ASTTests/Api/api.raml", project => {

            var expectedValue = 2
            var length = project.rootASTUnit.rootNode.elements("traits").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API resourceTypes"){
        runTest( "ASTTests/Api/api.raml", project => {

            var expectedValue = 2
            var length = project.rootASTUnit.rootNode.elements("resourceTypes").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }

    test("API securitySchemes"){
        runTest( "ASTTests/Api/api.raml", project => {

            var expectedValue = 2
            var length = project.rootASTUnit.rootNode.elements("securitySchemes").length
            if (length == expectedValue)
                succeed
            else
                fail(s"Expected value: $expectedValue, actual: ${length}")
        })
    }
}
