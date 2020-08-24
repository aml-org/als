package org.mulesoft.als.actions.codeactions.plugins.declarations

import amf.core.remote.Mimes
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.common.YamlWrapper.YNodeImplicits
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.YNode
import org.yaml.render.YamlRender

case class ExtractRAMLTypeCodeAction(params: CodeActionRequestParams, override val kind: CodeActionKind)
    extends ExtractSameFileDeclaration {

  override lazy val isApplicable: Boolean =
    vendor.isRaml && amfObject.isDefined && yPartBranch.exists(_.isKey) // && positionIsExtracted

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val renderLink: Option[YNode] = Some(YNode(newName).withKey("type"))
  override lazy val linkEntry: Option[TextEdit] =
    entryRange.map(
      TextEdit(
        _,
        s"\n${renderLink
          .map(YamlRender.render(_,
                                 entryIndentation +
                                   params.configuration.getFormatOptionForMime(Mimes.`APPLICATION/YAML`).indentationSize))
          .getOrElse(newName)}\n"
      ))
}

object ExtractRAMLTypeCodeAction extends CodeActionFactory {
  override val kind: CodeActionKind = CodeActionKind.RefactorExtract
  override final val title          = "Extract to Declaration"

  override def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    ExtractRAMLTypeCodeAction(params, kind)
}
