package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class XMLFacetInfo extends RAML10ASTTest {

  test("XMLFacetInfo attribute"){
    runTest( "ASTTests/XMLFacetInfo/XMLFacetInfo.raml", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("attribute").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLFacetInfo wrapped"){
    runTest( "ASTTests/XMLFacetInfo/XMLFacetInfo.raml", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("wrapped").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLFacetInfo name"){
    runTest( "ASTTests/XMLFacetInfo/XMLFacetInfo.raml", project => {

      var expectedValue = "fullname"
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLFacetInfo namespace"){
    runTest( "ASTTests/XMLFacetInfo/XMLFacetInfo.raml", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("namespace").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLFacetInfo prefix"){
    runTest( "ASTTests/XMLFacetInfo/XMLFacetInfo.raml", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("prefix").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
