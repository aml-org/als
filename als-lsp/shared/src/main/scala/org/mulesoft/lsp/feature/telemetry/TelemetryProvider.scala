package org.mulesoft.lsp.feature.telemetry

import java.util.UUID

import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MessageTypes extends Enumeration {
  type MessageTypes = String
  val BEGIN_PARSE         = "BEGIN_PARSE"
  val END_PARSE           = "END_PARSE"
  val BEGIN_PARSE_PATCHED = "BEGIN_PARSE_PATCHED"
  val END_PARSE_PATCHED   = "END_PARSE_PATCHED"
  val BEGIN_REPORT        = "BEGIN_REPORT"
  val END_REPORT          = "END_REPORT"
  val BEGIN_COMPLETION    = "BEGIN_COMPLETION"
  val END_COMPLETION      = "END_COMPLETION"
  val BEGIN_STRUCTURE     = "BEGIN_STRUCTURE"
  val END_STRUCTURE       = "END_STRUCTURE"
  val BEGIN_DIAGNOSTIC    = "BEGIN_DIAGNOSTIC"
  val END_DIAGNOSTIC      = "END_DIAGNOSTIC"
  val INDEX_DIALECT       = "INDEX_DIALECT"
  val BEGIN_RESOLUTION    = "BEGIN_RESOLUTION"
  val END_RESOLUTION      = "END_RESOLUTION"
}

trait TelemetryProvider {

  protected def addTimedMessage(code: String, messageType: MessageTypes, msg: String, uri: String, uuid: String): Unit

  final def timeProcess[T](code: String,
                           beginType: MessageTypes,
                           endType: MessageTypes,
                           msg: String,
                           uri: String,
                           fn: () => Future[T],
                           uuid: String = UUID.randomUUID().toString): Future[T] = {
    addTimedMessage(code, beginType, msg, uri, uuid)
    fn()
      .andThen { case _ => addTimedMessage(code, endType, msg, uri, uuid) }
  }
}
