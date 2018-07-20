package org.mulesoft.language.outline.test

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider

class ASTProvider(ast: IParseResult,
                  position: Int,
                  val language: String) extends IASTProvider {
    /**
      * Returns the root of AST
      * @return
      */
    def getASTRoot: Option[IHighLevelNode] = {
        return Some(this.ast.asInstanceOf[IHighLevelNode])
    }

    /**
      * Returns selected node.
      * @return
      */
    def getSelectedNode: Option[IParseResult] = {
        this.ast.getNodeByPosition(this.position)
    }
}