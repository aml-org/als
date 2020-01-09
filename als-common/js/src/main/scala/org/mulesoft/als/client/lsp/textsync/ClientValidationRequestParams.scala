package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.lsp.textsync.ValidationRequestParams

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientValidationRequestParams extends js.Object {
  def mainUri: String = js.native
}

object ClientValidationRequestParams {
  def apply(internal: ValidationRequestParams): ClientValidationRequestParams =
    js.Dynamic
      .literal(mainUri = internal.mainUri)
      .asInstanceOf[ClientValidationRequestParams]
}
// $COVERAGE-ON$