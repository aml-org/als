package org.mulesoft.als.server.modules.ast

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.modules.workspace.ReferenceOrigin

/**
  * AST listener
  */
trait AstListener[T] {

  /**
    * Called on new AST available
    *
    * @param ast     - AST
    * @param uuid    - telemetry UUID
    */
  def onNewAst(ast: T, uuid: String): Unit

  def onRemoveFile(uri: String): Unit
}

trait BaseUnitListener extends AstListener[(BaseUnit, Map[String, Set[ReferenceOrigin]])]

abstract class AstNotifier[T](val dependencies: List[AstListener[T]]) {
  protected def notify(bu: T, uuid: String): Unit = dependencies.foreach(_.onNewAst(bu, uuid))
}

trait TextListener {

  def notify(uri: String, kind: NotificationKind)

//  def withTextDocumentContainer(textDocumentContainer: TextDocumentContainer): Unit
}

sealed case class NotificationKind(kind: String)
object OPEN_FILE   extends NotificationKind("OPEN_FILE")
object FOCUS_FILE  extends NotificationKind("FOCUS_FILE")
object CHANGE_FILE extends NotificationKind("CHANGE_FILE")
object CLOSE_FILE  extends NotificationKind("CLOSE_FILE")
