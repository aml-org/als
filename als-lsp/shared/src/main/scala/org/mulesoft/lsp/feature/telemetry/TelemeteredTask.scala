package org.mulesoft.lsp.feature.telemetry

import java.util.UUID

import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.als.logger.Logger

import scala.concurrent.Future

trait TelemeteredTask[P, R] {

  protected def task(params: P): Future[R]

  protected def code(params: P): String
  protected def beginType(params: P): MessageTypes
  protected def endType(params: P): MessageTypes
  protected def msg(params: P): String
  protected def uri(params: P): String
  protected def uuid(params: P): String = UUID.randomUUID().toString

  final def run(params: P): Future[R] =
    Logger.timeProcess(
      code(params),
      beginType(params),
      endType(params),
      msg(params),
      uri(params),
      () => task(params),
      uuid(params)
    )
}
