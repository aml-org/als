package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind}
import org.mulesoft.lsp.feature.telemetry.TelemeteredTask

trait CodeActionPlugin[T] extends TelemeteredTask[CodeActionRequestParams, T] {
  val kind: CodeActionKind = CodeActionKind.Empty
  val isApplicable: Boolean
}

trait CodeActionResponsePlugin extends CodeActionPlugin[Seq[CodeAction]]