package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class PropertiesInFragment extends RAML10ASTTest {
  test("ExampleSpec value"){
    runTest( "ASTTests/PropertiesInFragment/api.raml", project => {
      val allTypesHaveTheirProperties = project.rootASTUnit.rootNode.elements("types")
        .forall(node => node.attributes.length == 2)

      if (allTypesHaveTheirProperties) succeed else fail("Expected both types to have both properties")
    })
  }
}
