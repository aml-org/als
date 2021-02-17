package org.mulesoft.als.actions.formatting

import amf.core.parser.{Range => ParserRange}
import org.mulesoft.als.common.YamlWrapper
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model._
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender, YamlRenderOptions}

case class RangeFormatting(parentYPart: YPart,
                           formattingOptions: FormattingOptions,
                           isJson: Boolean,
                           syntaxErrors: ErrorsCollected,
                           raw: Option[String]) {

  def format(): Seq[TextEdit] =
    formatPart(parentYPart)

  private def containsSyntaxError(ypart: YPart): Boolean =
    syntaxErrors.errors.exists(_.position.exists(err => ParserRange(ypart.range).contains(err.range)))

  private def formatPart(ypart: YPart): Seq[TextEdit] =
    if (containsSyntaxError(ypart)) {
      ypart.children.filterNot(_.isInstanceOf[YNonContent]).flatMap(formatPart)
    } else {
      ypart match {
        // todo: initial indentation for the value might be ignored if we emit an YMapEntry in YAML
        case map: YMapEntry if !isJson => format(YMap(ypart.location, IndexedSeq(map)))
        case e                         => format(e)
      }
    }

  private def format(yPart: YPart): Seq[TextEdit] = {
    val renderPart: YPart = yPart match {
      case doc: YDocument => doc.node // do not format head comment
      case _              => yPart
    }
    val initialIndentation =
      raw.map(t => YamlWrapper.getIndentation(t, renderPart.range.toPositionRange.start)).getOrElse(0)
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
        .dropWhile(_ == ' ')

    }

    Seq(TextEdit(range, s))

  }
}
