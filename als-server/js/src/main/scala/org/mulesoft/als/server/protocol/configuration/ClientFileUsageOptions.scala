package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.fileusage.FileUsageOptions

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientFileUsageOptions extends js.Object {

  def supported: Boolean = js.native
}

object ClientFileUsageOptions {
  def apply(internal: FileUsageOptions): ClientFileUsageOptions = {
    js.Dynamic
      .literal(
        supported = internal.supported
      )
      .asInstanceOf[ClientFileUsageOptions]
  }
}

// $COVERAGE-OFF$
