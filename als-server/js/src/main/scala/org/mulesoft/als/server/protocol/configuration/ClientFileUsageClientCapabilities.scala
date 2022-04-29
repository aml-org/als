package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.fileusage.FileUsageClientCapabilities

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientFileUsageClientCapabilities extends js.Object {
  def fileUsageSupport: Boolean = js.native
}

object ClientFileUsageClientCapabilities {
  def apply(internal: FileUsageClientCapabilities): ClientFileUsageClientCapabilities = {
    js.Dynamic
      .literal(
        fileUsageSupport = internal.fileUsageSupport
      )
      .asInstanceOf[ClientFileUsageClientCapabilities]
  }
}

// $COVERAGE-ON$
