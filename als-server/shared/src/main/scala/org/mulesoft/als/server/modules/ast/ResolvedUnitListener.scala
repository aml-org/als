package org.mulesoft.als.server.modules.ast

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.common.reconciler.{Reconciler, Runnable}
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfintegration.AmfImplicits._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ResolvedUnitListener extends AstListener[AmfResolvedUnit] with AccessUnits[AmfResolvedUnit] {
  type RunType <: Runnable[Unit]
  protected val logger: Logger
  protected val timeout              = 100
  private val reconciler: Reconciler = new Reconciler(logger, timeout)

  protected def runnable(ast: AmfResolvedUnit, uuid: String): RunType

  protected def onSuccess(uuid: String, uri: String): Unit
  protected def onFailure(uuid: String, uri: String, t: Throwable): Unit

  /**
    * Meant just for logging
    * @param resolved
    * @param uuid
    */
  protected def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit

  override final def onNewAst(ast: AmfResolvedUnit, uuid: String): Future[Unit] = {
    onNewAstPreprocess(ast, uuid)
    reconciler
      .schedule(runnable(ast, uuid))
      .future andThen {
      case Success(_)         => onSuccess(uuid, ast.baseUnit.identifier)
      case Failure(exception) => onFailure(uuid, ast.baseUnit.identifier, exception)
    }
  }
}
