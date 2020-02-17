package org.mulesoft.als.server.modules.ast

import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.als.server.workspace.{UnitRepositoriesManager, WorkspaceManager}
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.InitializableModule

/**
  * AST listener
  */
trait AstListener[T] {

  /**
    * Called on new AST available
    *
    * @param ast  - AST
    * @param uuid - telemetry UUID
    */
  def onNewAst(ast: T, uuid: String): Unit

  def onRemoveFile(uri: String): Unit

  def withUnitAccessor(unitAccesor: UnitRepositoriesManager): AstListener[T] = {
    this.unitAccessor = Some(unitAccesor)
    this
  }

  protected var unitAccessor: Option[UnitRepositoriesManager] = None
}

trait BaseUnitListener extends AstListener[(AmfParseResult, Map[String, DiagnosticsBundle])]

abstract class AstNotifier[T](val dependencies: List[AstListener[T]]) {
  protected def notify(bu: T, uuid: String): Unit = dependencies.foreach(_.onNewAst(bu, uuid))
}

trait TextListener {

  def notify(uri: String, kind: NotificationKind)
}

sealed case class NotificationKind(kind: String)

object OPEN_FILE extends NotificationKind("OPEN_FILE")

object FOCUS_FILE extends NotificationKind("FOCUS_FILE")

object CHANGE_FILE extends NotificationKind("CHANGE_FILE")

object CLOSE_FILE extends NotificationKind("CLOSE_FILE")

object CHANGE_CONFIG extends NotificationKind("CHANGE_CONFIG")
