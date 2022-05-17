package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

/** CodeActionKinds that this server may return.
  *
  * The list of kinds may be generic, such as `CodeActionKind.Refactor`, or the server may list out every specific kind
  * they provide.
  */
trait CodeActionOptions {

  val codeActionKinds: Option[Seq[CodeActionKind]]
}
