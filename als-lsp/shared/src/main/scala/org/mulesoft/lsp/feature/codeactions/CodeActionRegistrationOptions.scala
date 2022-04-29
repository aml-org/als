package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.common.TextDocumentRegistrationOptions

/** CodeActions options.
  *
  * @param codeActionKinds
  *   CodeActionKinds that this server may return.
  *
  * The list of kinds may be generic, such as `CodeActionKind.Refactor`, or the server may list out every specific kind
  * they provide.
  */
case class CodeActionRegistrationOptions(codeActionKinds: Option[Seq[CodeActionKind]] = None)
    extends CodeActionOptions
    with TextDocumentRegistrationOptions
