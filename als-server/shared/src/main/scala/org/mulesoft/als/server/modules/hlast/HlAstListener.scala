package org.mulesoft.als.server.modules.hlast

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * AST listener
  */
trait HlAstListener {

  /**
    * Called on new AST available
    *
    * @param uri     - document uri
    * @param version - document version
    * @param ast     - AST
    */
  def apply(uri: String, version: Int, ast: IParseResult): Unit
}
