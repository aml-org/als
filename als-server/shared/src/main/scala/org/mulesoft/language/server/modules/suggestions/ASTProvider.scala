package org.mulesoft.language.server.modules.suggestions

import amf.core.remote.Vendor
import org.mulesoft.als.suggestions.interfaces.{IASTProvider, Syntax}
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}

class ASTProvider(ast: IParseResult, val language: Vendor, val syntax: Syntax, val position: Int)
  extends IASTProvider {

  /**
    * Returns the root of AST
    *
    * @return
    */
  def getASTRoot: IHighLevelNode = this.ast.asInstanceOf[IHighLevelNode]

  /**
    * Returns selected node.
    *
    * @return
    */
  def getSelectedNode: Option[IParseResult] = this.ast.getNodeByPosition(this.position)

}
