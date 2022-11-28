package org.mulesoft.als.actions.formatting

import org.mulesoft.als.actions.formatting.SyamlImpl.YPartImpl
import org.mulesoft.als.common.ASTElementWrapper.AlsPositionRange
import org.mulesoft.als.common.dtoTypes.PositionRange
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
    raw: Option[String]
) {

  def format(): Seq[TextEdit] = formatPart(parentYPart)

  private def containsSyntaxError(part: YPart): Boolean =
    syntaxErrors.errors.exists(_.position.exists(err => part.range.contains(err.range)))

  private def formatPart(part: YPart): Seq[TextEdit] =
//    if (isJson && containsSyntaxError(part))
//      part.children.filterNot(_.isInstanceOf[YNonContent]).flatMap(formatPart)
//    else
    format(part)

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

  private def format(part: YPart): Seq[TextEdit] = {
    val initialIndentation: Int = part.range.start.column / formattingOptions.tabSize
    val renderPart: YPart       = part.format(formattingOptions.tabSize, initialIndentation)
    val range =
      if (isJson) LspRangeConverter.toLspRange(part.range.toPositionRange)
      else {
        val positionRange: PositionRange = part.range.toPositionRange
        LspRangeConverter.toLspRange(
          PositionRange(
            positionRange.start.moveColumn(-initialIndentation * formattingOptions.tabSize),
            positionRange.end
          )
        )
      }

    val s: String = if (isJson) {
      val renderOptions: JsonRenderOptions =
        JsonRenderOptions(formattingOptions.tabSize, formattingOptions.insertSpaces, applyFormatting = true)
      JsonRender.render(renderPart, 0, renderOptions) // todo: add some logic to guess desired indentation
    } else
      YamlRender
        .render(Seq(renderPart), expandReferences = false)

    Seq(TextEdit(range, applyOptions(s)))
  }
}
