package org.mulesoft.als.server.modules.structure

import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider

class ASTProvider(ast: IParseResult, position: Int, val language: String) extends IASTProvider {

  /**
    * Returns the root of AST
    *
    * @return
    */
  def getASTRoot: Option[IHighLevelNode] = Some(this.ast.asInstanceOf[IHighLevelNode])

  /**
    * Returns selected node.
    *
    * @return
    */
  def getSelectedNode: Option[IParseResult] = this.ast.getNodeByPosition(this.position)
}
