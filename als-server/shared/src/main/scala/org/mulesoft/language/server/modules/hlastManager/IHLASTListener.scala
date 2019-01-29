package org.mulesoft.language.server.modules.hlastManager

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * AST listener
  */
trait IHLASTListener {

  /**
    * Called on new AST available
    *
    * @param uri     - document uri
    * @param version - document version
    * @param ast     - AST
    */
  def apply(uri: String, version: Int, ast: IParseResult): Unit
}
