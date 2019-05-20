package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.common.dtoTypes.PositionRange

trait Suggestion {
  def text: String

  def description: String

  def displayText: String

  def prefix: String

  def category: String

  def trailingWhitespace: String

  def range: Option[PositionRange]
}
