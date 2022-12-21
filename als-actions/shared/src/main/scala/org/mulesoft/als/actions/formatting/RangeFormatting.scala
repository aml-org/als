package org.mulesoft.als.actions.formatting

import org.mulesoft.als.actions.formatting.SyamlImpl.YPartImpl
import org.mulesoft.als.common.ASTElementWrapper.AlsPositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender}

case class RangeFormatting(
    parentYPart: YPart,
    formattingOptions: FormattingOptions,
    isJson: Boolean,
    syntaxErrors: ErrorsCollected,
    raw: Option[String],
    initialIndentation: Int
) {

  def format(applyOptions: Boolean = true): Seq[TextEdit] = formatPart(parentYPart, applyOptions)

  private def containsSyntaxError(part: YPart): Boolean =
    syntaxErrors.errors.exists(_.position.exists(err => part.range.contains(err.range)))

  private def formatPart(part: YPart, applyOptions: Boolean = true): Seq[TextEdit] =
    if (isJson && containsSyntaxError(part))
      part.children
        .filterNot(_.isInstanceOf[YNonContent])
        .flatMap(formatPart(_, applyOptions))
    else
      format(part, applyOptions)

  def applyOptions(s: String): String = {
    var formatted = s
    if (formattingOptions.getTrimTrailingWhitespace)
      formatted = formatted.replaceAll("""(?m)[^\S\r\n]+$""", "") // strip spaces end of line except after colon
    if (formattingOptions.getTrimFinalNewlines)
      formatted = formatted.replaceAll("""\n+$""", "\n") // reduce trailing EOL
    if (formattingOptions.getInsertFinalNewline && !formatted.endsWith("\n"))
      formatted += "\n" // if no final EOL, add one
    formatted
  }

  private def format(part: YPart, mustApplyOptions: Boolean): Seq[TextEdit] = {
    val renderPart: YPart = part.format(formattingOptions.tabSize, initialIndentation)
    val range             = LspRangeConverter.toLspRange(part.range.toPositionRange)

    val s: String = if (isJson) {
      val renderOptions: JsonRenderOptions =
        JsonRenderOptions(formattingOptions.tabSize, formattingOptions.insertSpaces, applyFormatting = true)
      JsonRender.render(
        renderPart,
        initialIndentation,
        renderOptions
      ) // todo: add some logic to guess desired indentation
    } else
      YamlRender
        .render(Seq(renderPart), expandReferences = false)

    if (mustApplyOptions)
      Seq(TextEdit(range, applyOptions(s)))
    else
      Seq(TextEdit(range, s))
  }
}
