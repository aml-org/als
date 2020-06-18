package org.mulesoft.lsp.feature

import org.mulesoft.lsp.feature.telemetry.TelemeteredTask

import scala.concurrent.Future

trait TelemeteredRequestHandler[P, R] extends RequestHandler[P, R] with TelemeteredTask[P, R] {
  override final def apply(params: P): Future[R] =
    run(params)
}
