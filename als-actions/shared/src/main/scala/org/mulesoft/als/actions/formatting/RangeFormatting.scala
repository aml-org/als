package org.mulesoft.als.actions.formatting

import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model.{YDocument, YPart}
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

case class RangeFormatting(yPart: YPart,
                           formattingOptions: FormattingOptions,
                           initialIndentation: Int,
                           isJson: Boolean) {

  def format(): Seq[TextEdit] = {
    val renderPart: YPart = yPart match {
      case doc: YDocument => doc.node // do not format head comment
      case _              => yPart
    }

    val range = LspRangeConverter.toLspRange(renderPart.range.toPositionRange)

    val s: String = if (isJson) {
      val renderOptions: JsonRenderOptions =
        JsonRenderOptions(formattingOptions.indentationSize, formattingOptions.insertSpaces, applyFormatting = true)
      JsonRender.render(renderPart, initialIndentation, renderOptions)
    } else {
      val renderOptions: YamlRenderOptions =
        YamlRenderOptions(formattingOptions.indentationSize, applyFormatting = true)
      YamlRender
        .render(Seq(renderPart), expandReferences = false, renderOptions, initialIndentation)
        .replaceFirst("^\\s+", "")

    }

    Seq(TextEdit(range, s))

  }
}
