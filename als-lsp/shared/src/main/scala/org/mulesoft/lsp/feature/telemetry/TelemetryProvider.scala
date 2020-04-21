package org.mulesoft.lsp.feature.telemetry

import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes

object MessageTypes extends Enumeration {
  type MessageTypes = Value
  val BEGIN_PARSE, END_PARSE, BEGIN_PARSE_PATCHED, END_PARSE_PATCHED, BEGIN_REPORT, END_REPORT, BEGIN_PATCHING,
  END_PATCHING, GOT_DIAGNOSTICS, BEGIN_COMPLETION, END_COMPLETION, BEGIN_STRUCTURE, END_STRUCTURE, BEGIN_DIAGNOSTIC,
  END_DIAGNOSTIC, CHANGE_DOCUMENT, BEGIN_AMF_INIT, END_AMF_INIT, INDEX_DIALECT, DIAGNOSTIC_ERROR, BEGIN_RESOLUTION,
  END_RESOLUTION, RESOLUTION = Value
}

trait TelemetryProvider {

  def addTimedMessage(code: String, messageType: MessageTypes, msg: String, uri: String, uuid: String): Unit

  def addTimedMessage(msg: String,
                      className: String,
                      methodName: String,
                      messageType: MessageTypes,
                      uri: String,
                      uuid: String): Unit =
    addTimedMessage(s"$className : $methodName", messageType, msg, uri, uuid)
}
