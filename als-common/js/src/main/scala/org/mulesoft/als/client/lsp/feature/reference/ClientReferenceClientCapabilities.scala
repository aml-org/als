package org.mulesoft.als.client.lsp.feature.reference

import org.mulesoft.lsp.feature.reference.ReferenceClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientReferenceClientCapabilities extends js.Object {
  def dynamicRegistration: UndefOr[Boolean] = js.native
}

object ClientReferenceClientCapabilities {
  def apply(internal: ReferenceClientCapabilities): ClientReferenceClientCapabilities =
    js.Dynamic
      .literal(dynamicRegistration = internal.dynamicRegistration.orUndefined)
      .asInstanceOf[ClientReferenceClientCapabilities]
}
// $COVERAGE-ON$