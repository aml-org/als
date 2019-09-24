package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.{CompletionItem, InsertTextFormat}
import org.mulesoft.lsp.feature.completion.InsertTextFormat.InsertTextFormat

class CompletionItemBuilder(_range: PositionRange) {
  private var text: String         = ""
  private var description: String  = ""
  private var displayText: String  = ""
  private var prefix: String       = ""
  private var range: PositionRange = _range
  private var insertTextFormat     = InsertTextFormat.PlainText
  private var category: String     = ""

  def withText(text: String): this.type = {
    this.text = text
    this
  }

  def withDescription(description: String): this.type = {
    this.description = description
    this
  }

  def withDisplayText(displayText: String): this.type = {
    this.displayText = displayText
    this
  }

  def withPrefix(prefix: String): this.type = {
    this.prefix = prefix
    this
  }

  def withRange(range: PositionRange): this.type = {
    this.range = range
    this
  }

  def withInsertTextFormat(insertTextFormat: InsertTextFormat): this.type = {
    this.insertTextFormat = insertTextFormat
    this
  }

  def withCategory(category: String): this.type = {
    this.category = category
    this
  }

  def getRange: PositionRange = this.range
  def getDisplayText: String  = this.displayText
  def getText: String         = this.text

  def build(): CompletionItem = {
    val t: (Option[String], String) = if (text.contains("\n")) (Some(text), null) else (None, text)

    CompletionItem(
      displayText,
      textEdit = textEdit(t._2, range),
      insertText = t._1,
      detail = Some(category),
      documentation = Some(description),
      insertTextFormat = Some(insertTextFormat)
    )
  }

  private def textEdit(text: String, range: PositionRange): Option[TextEdit] = {
    if (text == null || text.isEmpty) None
    else Some(TextEdit(LspRangeConverter.toLspRange(range), text))
  }
}
