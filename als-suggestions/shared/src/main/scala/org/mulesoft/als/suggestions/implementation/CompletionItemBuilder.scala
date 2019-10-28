package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.InsertTextFormat.InsertTextFormat
import org.mulesoft.lsp.feature.completion.{CompletionItem, InsertTextFormat}

class CompletionItemBuilder(r: PositionRange) {
  private var text: String               = ""
  private var description: String        = ""
  private var displayText: String        = ""
  private var prefix: String             = ""
  private var range: PositionRange       = r
  private var insertTextFormat           = InsertTextFormat.PlainText
  private var category: String           = ""
  private var template                   = false
  private var filterText: Option[String] = None

  def withText(text: String): this.type = {
    this.text = text
    this
  }

  def withFilterText(filterText: String): this.type = {
    this.filterText = Some(filterText)
    this
  }

  def withTemplate(): this.type = {
    template = true
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

  def getPriority(text: String): Int =
    (if (template) 20 else 10) + { if (text.startsWith("(")) 10 else 0 }

  def build(): CompletionItem =
    CompletionItem(
      displayText,
      textEdit = textEdit(text, range),
      detail = Some(category),
      documentation = Some(description),
      insertTextFormat = Some(insertTextFormat),
      sortText = Some(s"${getPriority(text)}$displayText"),
      filterText = filterText.orElse(Some(text))
    )

  private def textEdit(text: String, range: PositionRange): Option[TextEdit] = {
    if (text == null || text.isEmpty) None
    else Some(TextEdit(LspRangeConverter.toLspRange(range), text))
  }
}
