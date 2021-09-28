package org.mulesoft.lsp.textsync

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDependencyConfiguration extends js.Object {
  def file: String  = js.native
  def scope: String = js.native
}

object ClientDependencyConfiguration {
  def apply(internal: DependencyConfiguration): ClientDependencyConfiguration = {
    js.Dynamic
      .literal(
        file = internal.file,
        scope = internal.scope
      )
      .asInstanceOf[ClientDependencyConfiguration]
  }
}

// $COVERAGE-ON$
