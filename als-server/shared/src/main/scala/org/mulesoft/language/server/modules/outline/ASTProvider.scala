package org.mulesoft.language.server.modules.outline

import amf.core.remote.Vendor
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider

class ASTProvider(ast: IParseResult, position: Int, val language: String) extends IASTProvider {

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
