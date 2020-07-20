package org.mulesoft.als.server.protocol.actions

import org.mulesoft.als.server.feature.renameFile.RenameFileActionOptions

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientRenameFileActionServerOptions extends js.Object {

  def supported: Boolean = js.native
}

object ClientRenameFileActionServerOptions {
  def apply(internal: RenameFileActionOptions): ClientRenameFileActionServerOptions = {
    js.Dynamic
      .literal(
        supported = internal.supported
      )
      .asInstanceOf[ClientRenameFileActionServerOptions]
  }
}
// $COVERAGE-ON$
