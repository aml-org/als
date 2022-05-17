package org.mulesoft.lsp.feature.documentsymbol

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientLocation

import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSymbolInformation extends js.Object {
  def name: String                   = js.native
  def kind: Int                      = js.native
  def location: ClientLocation       = js.native
  def containerName: UndefOr[String] = js.native
  def deprecated: UndefOr[Boolean]   = js.native
}

object ClientSymbolInformation {
  def apply(internal: SymbolInformation): ClientSymbolInformation =
    js.Dynamic
      .literal(
        name = internal.name,
        kind = internal.kind.id,
        location = internal.location.toClient,
        containerName = internal.containerName.orUndefined,
        deprecated = internal.deprecated.orUndefined
      )
      .asInstanceOf[ClientSymbolInformation]
}
// $COVERAGE-ON$
