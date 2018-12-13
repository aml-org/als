package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}

/**
  * Provides AST, this module operates upon
  */
trait IASTProvider {

  /**
    * Returns the root of AST
    * @return
    */
  def getASTRoot: Option[IHighLevelNode]

  /**
    * Returns selected node.
    * @return
    */
  def getSelectedNode: Option[IParseResult]

  def language: String
}
