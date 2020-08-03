package org.mulesoft.als.server.protocol.actions

import org.mulesoft.als.server.feature.renamefile.RenameFileActionParams
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRenameFileActionParams extends js.Object {
  def oldDocument: ClientTextDocumentIdentifier = js.native
  def newDocument: ClientTextDocumentIdentifier = js.native
}

object ClientRenameFileActionParams {
  def apply(internal: RenameFileActionParams): ClientRenameFileActionParams = {
    js.Dynamic
      .literal(
        oldDocument = internal.oldDocument.toClient,
        newDocument = internal.newDocument.toClient
      )
      .asInstanceOf[ClientRenameFileActionParams]
  }
}

// $COVERAGE-ON$
