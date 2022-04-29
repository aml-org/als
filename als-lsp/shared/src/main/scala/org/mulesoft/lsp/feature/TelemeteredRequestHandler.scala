package org.mulesoft.lsp.feature

import org.mulesoft.exceptions.AlsException
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemeteredTask}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TelemeteredRequestHandler[P, R] extends RequestHandler[P, R] with TelemeteredTask[P, R] {

  /** If Some(_), this will be sent as a response as a default for a managed exception
    */
  protected val empty: Option[R]

  override final def apply(params: P): Future[R] =
    try {
      run(params)
        .recoverWith { case e: AlsException =>
          manageException(e)
        }
    } catch {
      case e: AlsException => manageException(e)
    }

  private def manageException(e: AlsException) = {
    telemetry.addErrorMessage("TelemeteredException", e.getMessage, e.getUri, e.getUuid)
    empty match {
      case Some(emptyResponse) =>
        Future(emptyResponse)
      case None =>
        throw e
    }
  }
}
