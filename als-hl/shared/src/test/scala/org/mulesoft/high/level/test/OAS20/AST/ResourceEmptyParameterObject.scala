package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ResourceEmptyParameterObject extends OAS20ASTTest {

  test("ResourceEmptyParameterObject position") {
    runTest(
      "ASTTests/EmptyParameter/EmptyParameter.json",
      project => {
        val actualValue: IHighLevelNode =
          project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head
        val eolDelta = if (project.rootASTUnit.positionsMapper.getText.exists(_ == '\r')) 10 else 0 // Delta for Windows EOL (\r\n)
        assert(actualValue.sourceInfo.containsPosition(166 + eolDelta))
        assert(!actualValue.sourceInfo.containsPosition(176 + eolDelta))
      }
    )
  }
}
