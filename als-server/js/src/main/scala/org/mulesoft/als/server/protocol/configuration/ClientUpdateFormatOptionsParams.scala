package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.{UpdateFormatOptionsParams => InternalUpdateFormatOptionsParams}

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientUpdateFormatOptionsParams extends js.Object {
  def tabSize: Int          = js.native
  def insertSpaces: Boolean = js.native
}

object ClientUpdateFormatOptionsParams {
  def apply(internal: InternalUpdateFormatOptionsParams): ClientUpdateFormatOptionsParams = {
    js.Dynamic
      .literal(
        tabSize = internal.tabSize,
        insertSpaces = internal.insertSpaces
      )
      .asInstanceOf[ClientUpdateFormatOptionsParams]
  }
}

// $COVERAGE-ON$
