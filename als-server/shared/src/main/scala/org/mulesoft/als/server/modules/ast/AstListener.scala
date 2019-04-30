package org.mulesoft.als.server.modules.ast

import amf.core.model.document.BaseUnit

/**
  * AST listener
  */
trait AstListener {

  /**
    * Called on new AST available
    *
    * @param uri     - document uri
    * @param version - document version
    * @param ast     - AST
    */
  def apply(uri: String, version: Int, ast: BaseUnit): Unit
}
