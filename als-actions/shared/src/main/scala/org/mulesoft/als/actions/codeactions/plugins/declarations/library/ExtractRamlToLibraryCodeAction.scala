package org.mulesoft.als.actions.codeactions.plugins.declarations.library

import amf.core.internal.remote.Spec
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.logger.Logger
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

/** 1- Check every declared in range (just first level from root?) 2- Add each element to Module() along with new
  * Location 3- Add "uses" key for new file 4- change each reference for each declared and add `$alias.` at the start of
  * the label
  * @param params
  */
case class ExtractRamlToLibraryCodeAction(params: CodeActionRequestParams) extends ExtractDeclarationsToLibrary {

  override lazy val isApplicable: Boolean =
    params.bu.sourceSpec.contains(Spec.RAML10) && selectedElements.nonEmpty

  override protected def telemetry: TelemetryProvider = Logger.delegateTelemetryProvider.get

  override protected val kindTitle: CodeActionKindTitle = ExtractRamlToLibraryCodeAction
}

object ExtractRamlToLibraryCodeAction extends CodeActionFactory with ExtractToLibraryKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new ExtractRamlToLibraryCodeAction(params)
}
