package org.mulesoft.als.suggestions.test

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.client.Suggestion
import upickle.default.{macroRW, ReadWriter => RW}

case class SuggestionPosition(line: Int, character: Int)

object SuggestionPosition {

  implicit def rw: RW[SuggestionPosition] = macroRW

  implicit def sharedToTransport(from: Position): SuggestionPosition = {
    SuggestionPosition(from.line, from.column)
  }
}

case class SuggestionRange(start: SuggestionPosition, end: SuggestionPosition)

object SuggestionRange {

  implicit def rw: RW[SuggestionRange] = macroRW

  implicit def sharedToTransport(from: PositionRange): SuggestionRange =
    SuggestionRange(from.start, from.end)
}

case class SuggestionNode(text: String,
                          description: String,
                          displayText: String,
                          prefix: String,
                          category: String,
                          range: Option[SuggestionRange])

object SuggestionNode {

  implicit def rw: RW[SuggestionNode] = macroRW

  implicit def sharedToTransport(from: Suggestion): SuggestionNode =
    SuggestionNode(from.text,
                   from.description,
                   from.displayText,
                   from.prefix,
                   from.category,
                   from.range.map(r => SuggestionRange.sharedToTransport(r)))
}
