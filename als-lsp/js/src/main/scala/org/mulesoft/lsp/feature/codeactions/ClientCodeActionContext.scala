package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.diagnostic.ClientDiagnostic
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionContext extends js.Object {
  def diagnostics: js.Array[ClientDiagnostic] = js.native
  def only: js.UndefOr[js.Array[Int]]         = js.native
}

object ClientCodeActionContext {
  def apply(internal: CodeActionContext): ClientCodeActionContext =
    js.Dynamic
      .literal(
        diagnostics = internal.diagnostics.map(_.toClient).toJSArray,
        only = internal.only.map(a => a.map(_.id).toJSArray).orUndefined
      )
      .asInstanceOf[ClientCodeActionContext]
}

// $COVERAGE-ON$
