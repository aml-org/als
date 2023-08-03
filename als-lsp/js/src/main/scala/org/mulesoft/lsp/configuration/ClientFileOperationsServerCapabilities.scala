package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientFileOperationRegistrationOptionsConverter

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileOperationsServerCapabilities extends js.Object {
  val didCreate: js.UndefOr[ClientFileOperationRegistrationOptions]  = js.native
  val willCreate: js.UndefOr[ClientFileOperationRegistrationOptions] = js.native
  val didRename: js.UndefOr[ClientFileOperationRegistrationOptions]  = js.native
  val willRename: js.UndefOr[ClientFileOperationRegistrationOptions] = js.native
  val didDelete: js.UndefOr[ClientFileOperationRegistrationOptions]  = js.native
  val willDelete: js.UndefOr[ClientFileOperationRegistrationOptions] = js.native
}
object ClientFileOperationsServerCapabilities {
  def apply(internal: FileOperationsServerCapabilities): ClientFileOperationsServerCapabilities =
    js.Dynamic
      .literal(
        didCreate = internal.didCreate.map(_.toClient).orUndefined,
        willCreate = internal.willCreate.map(_.toClient).orUndefined,
        didRename = internal.didRename.map(_.toClient).orUndefined,
        willRename = internal.willRename.map(_.toClient).orUndefined,
        didDelete = internal.didDelete.map(_.toClient).orUndefined,
        willDelete = internal.willDelete.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientFileOperationsServerCapabilities]
}

// $COVERAGE-ON$
