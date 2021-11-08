package org.mulesoft.als.server.modules.ast

import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.{ProjectConfiguration, UnitAccessor}
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.amfconfiguration.AmfParseResult

import scala.concurrent.Future

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
  def onNewAst(ast: T, uuid: String): Future[Unit]

  def isActive: Boolean = true

  def onRemoveFile(uri: String): Unit
}

trait AccessUnits[T] {
  def withUnitAccessor(unitAccessor: UnitAccessor[T]): AccessUnits[T] = {
    this.unitAccessor = Some(unitAccessor)
    this
  }
  protected var unitAccessor: Option[UnitAccessor[T]] = None
}

case class BaseUnitListenerParams(parseResult: AmfParseResult,
                                  diagnosticsBundle: Map[String, DiagnosticsBundle],
                                  tree: Boolean,
                                  isDependency: Boolean = false)

trait BaseUnitListener extends AstListener[BaseUnitListenerParams] with AccessUnits[CompilableUnit]

trait TextListener {

  def notify(uri: String, kind: NotificationKind): Future[Unit]

  def newConfig(projectConfig: ProjectConfiguration): Future[Unit]
}

sealed case class NotificationKind(kind: String)

object OPEN_FILE extends NotificationKind("OPEN_FILE")

object FOCUS_FILE extends NotificationKind("FOCUS_FILE")

object CHANGE_FILE extends NotificationKind("CHANGE_FILE")

object CLOSE_FILE extends NotificationKind("CLOSE_FILE")

object CHANGE_CONFIG extends NotificationKind("CHANGE_CONFIG")

object WORKSPACE_TERMINATED extends NotificationKind("WORKSPACE_TERMINATED")
