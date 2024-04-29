package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

/** @param valueSet:
  *   The code action kind values the client supports. When this property exists the client also guarantees that it will
  *   handle values outside its set gracefully and falls back to a default value when unknown.
  */
case class CodeActionKindCapabilities(valueSet: List[CodeActionKind])
