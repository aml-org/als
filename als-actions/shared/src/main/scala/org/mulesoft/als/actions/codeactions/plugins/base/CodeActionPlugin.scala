package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.lsp.feature.telemetry.TelemeteredTask

trait CodeActionPlugin[T] extends TelemeteredTask[CodeActionRequestParams, T] {
  val isApplicable: Boolean
}

trait CodeActionResponsePlugin extends CodeActionPlugin[Seq[AbstractCodeAction]]
