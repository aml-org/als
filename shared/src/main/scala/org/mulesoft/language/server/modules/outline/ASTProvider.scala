import org.mulesoft.high.level.project.IProject
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider

class ASTProvider(ast: IParseResult) extends IASTProvider{
  /**
    * Returns the root of AST
    * @return
    */
  def getASTRoot: Option[IHighLevelNode] = {
    return this.ast
  }

  /**
    * Returns selected node.
    * @return
    */
  def getSelectedNode: Option[IParseResult] = {
    return this.ast
  }

}