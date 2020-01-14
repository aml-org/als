package org.mulesoft.als.nodeclient

import org.mulesoft.als.server.ClientLogger

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

object JsPrintLnLogger {
  def apply(): ClientLogger =
    js.Dynamic
      .literal(
        error = (message: String) => println(message),
        warn = (message: String) => println(message),
        info = (message: String) => println(message),
        log = (message: String) => println(message),
      )
      .asInstanceOf[ClientLogger]
}

// $COVERAGE-ON$
