package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.configuration.ProjectConfigurationStyle

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientProjectConfigurationStyle extends js.Object {
  def style: String = js.native
}

object ClientProjectConfigurationStyle {
  def apply(internal: ProjectConfigurationStyle): ClientProjectConfigurationStyle = {
    js.Dynamic
      .literal(
        style = internal.style.toString
      )
      .asInstanceOf[ClientProjectConfigurationStyle]
  }
}

// $COVERAGE-ON$
