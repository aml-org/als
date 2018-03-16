package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class LibraryBase extends RAML10ASTTest {

  test("LibraryBase types") {
    runTest("ASTTests/LibraryBase/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("types").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("LibraryBase traits") {
    runTest("ASTTests/LibraryBase/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("traits").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("LibraryBase resourceTypes") {
    runTest("ASTTests/LibraryBase/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("LibraryBase annotationTypes") {
    runTest("ASTTests/LibraryBase/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("LibraryBase securitySchemes") {
    runTest("ASTTests/LibraryBase/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
