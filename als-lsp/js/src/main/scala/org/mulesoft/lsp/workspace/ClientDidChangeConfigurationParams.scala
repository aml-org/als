package org.mulesoft.lsp.workspace

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeConfigurationParams extends js.Object {
  def settings: js.Object = js.native
}

object ClientDidChangeConfigurationParams {
  def apply(internal: DidChangeConfigurationParams): ClientDidChangeConfigurationParams = {
    val s: js.Object = internal.settings match {
      case js: js.Object => js
//      case _ => // not implemented?
    }
    js.Dynamic
      .literal(settings = s)
      .asInstanceOf[ClientDidChangeConfigurationParams]
  }
}
// $COVERAGE-ON$
