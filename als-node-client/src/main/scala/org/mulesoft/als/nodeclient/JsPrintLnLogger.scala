package org.mulesoft.als.nodeclient

import org.mulesoft.als.server.JsClientLogger

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

object JsPrintLnLogger {
  def apply(): JsClientLogger =
    js.Dynamic
      .literal(
        error = (message: String) => println(message),
        warn = (message: String) => println(message),
        info = (message: String) => println(message),
        log = (message: String) => println(message)
      )
      .asInstanceOf[JsClientLogger]
}

// $COVERAGE-ON$
