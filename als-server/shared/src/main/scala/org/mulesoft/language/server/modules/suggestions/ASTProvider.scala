package org.mulesoft.language.server.modules.suggestions
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.als.suggestions.interfaces.IASTProvider
import amf.core.remote.Vendor
import org.mulesoft.als.suggestions.interfaces.Syntax

class ASTProvider(ast: IParseResult,
                  val language:Vendor,
                  val syntax: Syntax,
                  val position: Int) extends IASTProvider {
  /**
    * Returns the root of AST
    * @return
    */
  def getASTRoot: IHighLevelNode = {
    return this.ast.asInstanceOf[IHighLevelNode]
  }

  /**
    * Returns selected node.
    * @return
    */
  def getSelectedNode: Option[IParseResult] = {
    this.ast.getNodeByPosition(this.position)
  }

}