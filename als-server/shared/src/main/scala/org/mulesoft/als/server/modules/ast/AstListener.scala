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
    * @param uuid    - telemetry UUID
    */
  def apply(uri: String, version: Int, ast: BaseUnit, uuid: String): Unit
}
