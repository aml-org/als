package org.mulesoft.als.client.lsp.feature.reference

import org.mulesoft.lsp.feature.reference.ReferenceContext

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientReferenceContext extends js.Object {
  def includeDeclaration: Boolean = js.native
}

object ClientReferenceContext {
  def apply(internal: ReferenceContext): ClientReferenceContext =
    js.Dynamic
      .literal(includeDeclaration = internal.includeDeclaration)
      .asInstanceOf[ClientReferenceContext]
}

// $COVERAGE-ON$