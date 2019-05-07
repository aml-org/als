package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class Reference extends RAML08ASTTest {

  test("Reference name") {
    runTest(
      "ASTTests/Reference/Reference.raml",
      project => {
        val eol           = if (project.rootASTUnit.positionsMapper.getText.exists(_ == '\r')) "\r\n" else "\n" // Windows EOL (\r\n)
        val expectedValue = s"content$eol"
        val length        = project.rootASTUnit.rootNode.elements("documentation").head.attribute("content").get.value
        if (length == Some(expectedValue))
          succeed
        else
          fail(s"Expected value: $expectedValue, actual: ${length}")
      }
    )
  }
}
