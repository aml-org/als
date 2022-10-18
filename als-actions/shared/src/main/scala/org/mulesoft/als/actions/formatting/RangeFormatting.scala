package org.mulesoft.als.actions.formatting

import org.mulesoft.als.actions.formatting.SyamlImpl.YPartImpl
import org.mulesoft.als.common.ASTElementWrapper
import org.mulesoft.als.common.ASTElementWrapper.AlsPositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

case class RangeFormatting(
    parentYPart: YPart,
    formattingOptions: FormattingOptions,
    isJson: Boolean,
    syntaxErrors: ErrorsCollected,
    raw: Option[String]
) {

  def format(): Seq[TextEdit] =
    formatPart(parentYPart)

  private def containsSyntaxError(ypart: YPart): Boolean =
    syntaxErrors.errors.exists(_.position.exists(err => ypart.range.contains(err.range)))

  private def formatPart(ypart: YPart): Seq[TextEdit] =
    if (isJson && containsSyntaxError(ypart))
      ypart.children.filterNot(_.isInstanceOf[YNonContent]).flatMap(formatPart)
    else format(ypart)

  private def format(yPart: YPart): Seq[TextEdit] = {
    val renderPart: YPart = yPart.format(formattingOptions.tabSize)

    val initialIndentation =
      raw.map(t => ASTElementWrapper.getIndentation(t, renderPart.range.toPositionRange.start)).getOrElse(0)
    val range = LspRangeConverter.toLspRange(renderPart.range.toPositionRange)

    val s: String = if (isJson) {
      val renderOptions: JsonRenderOptions =
        JsonRenderOptions(formattingOptions.tabSize, formattingOptions.insertSpaces, applyFormatting = true)
      JsonRender.render(renderPart, initialIndentation, renderOptions)
    } else {
      val renderOptions: YamlRenderOptions =
        YamlRenderOptions(formattingOptions.tabSize, applyFormatting = false)
      YamlRender
        .render(Seq(renderPart), expandReferences = false, renderOptions, initialIndentation)
    }

    Seq(TextEdit(range, s))
  }
}
