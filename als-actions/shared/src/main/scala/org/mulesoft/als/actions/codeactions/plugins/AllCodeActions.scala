package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.testaction.TestCodeAction

object AllCodeActions {
  def all: Seq[CodeActionFactory] = Seq(TestCodeAction)
}