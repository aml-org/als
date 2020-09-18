package org.mulesoft.als.actions.codeactions.plugins.declarations

import amf.core.remote.Mimes
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.{YMap, YMapEntry, YNode, YPart, YType}
import org.yaml.render.YamlRender

import scala.annotation.tailrec

case class ExtractRAMLTypeCodeAction(params: CodeActionRequestParams, override val kind: CodeActionKind)
    extends ExtractSameFileDeclaration {

  override lazy val isApplicable: Boolean =
    vendor.isRaml && positionIsExtracted && amfObject.exists(o => declarationPath(o, params.dialect) == Seq("types"))

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected lazy val entryIndentation: Int =
    getActualIndentation(entryAst) + indentIfNecessary

  /**
    * cases for inlined types
    * @return
    */
  private def indentIfNecessary =
    amfObject
      .map(_.annotations.ast() match {
        case Some(e: YMapEntry) if e.value.tagType != YType.Map => indentationSize
        case _                                                  => 0
      })
      .getOrElse(0)

  private lazy val indentationSize =
    params.configuration.getFormatOptionForMime(Mimes.`APPLICATION/YAML`).indentationSize

  override lazy val linkEntry: Option[TextEdit] =
    entryRange.map(
      TextEdit(
        _,
        s"\n${renderLink
          .map(YamlRender.render(_, entryIndentation))
          .getOrElse(newName)}\n"
      ))

  /**
    * If its an entry check the start position for key, else check if I can get a close entry, else check my parent entry
    */
  @tailrec
  protected final def getActualIndentation(p: Option[YPart]): Int =
    p match {
      case Some(e: YMapEntry) => e.key.range.columnFrom
      case Some(n: YNode)     => getActualIndentation(Some(n.value))
      case Some(m: YMap)      => getActualIndentation(m.entries.headOption)
      case Some(_)            => getActualIndentation(yPartBranch.flatMap(_.parentEntry))
      case _                  => 0
    }
}

object ExtractRAMLTypeCodeAction extends CodeActionFactory {
  override val kind: CodeActionKind = CodeActionKind.RefactorExtract
  override final val title          = "Extract to Declaration"

  override def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    ExtractRAMLTypeCodeAction(params, kind)
}
