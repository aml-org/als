package org.mulesoft.als.server.modules.ast

import amf.core.model.document.BaseUnit

/**
  * AST listener
  */
trait AstListener[T] {

  /**
    * Called on new AST available
    *
    * @param uri     - document uri
    * @param version - document version
    * @param ast     - AST
    * @param uuid    - telemetry UUID
    */
  def onNewAst(ast: T, uuid: String): Unit
}

trait BaseUnitListener extends AstListener[BaseUnit]

abstract class AstNotifier[T](val dependencies: List[AstListener[T]]) {
  protected def notify(bu: T, uuid: String): Unit = dependencies.foreach(_.onNewAst(bu, uuid))
}

trait TextListener {
  def indexDialect(uri: String, content: Option[String]): Unit

  def onFocus(uri: String): Unit
  def trigger(uri: String)
}
