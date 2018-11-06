package org.mulesoft.high.level.test.AML.AST

import org.mulesoft.high.level.test.AML.DialectASTTest

class BasicTests extends DialectASTTest{
    override def rootPath: String = "AML/ASTTests"

    test("test 001"){
        runTest( "test001/dialect6.yaml", "test001/example6.yaml", project => {

            val expectedValue = "1.0.0"
            project.rootASTUnit.rootNode.attribute("asyncapi") match {
                case Some(a) => a.value match {
                    case Some(expectedValue) => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail("'asyncapi' attribute not found")
            }
        })
    }
}
