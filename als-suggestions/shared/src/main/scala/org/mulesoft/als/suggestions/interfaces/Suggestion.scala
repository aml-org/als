package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.feature.completion.InsertTextFormat

trait Suggestion {
  def text: String

  def description: String

  def displayText: String

  def prefix: String

  def category: String

  def trailingWhitespace: String

  def range: Option[PositionRange]

  def insertTextFormat: InsertTextFormat.Value = InsertTextFormat.PlainText
}
