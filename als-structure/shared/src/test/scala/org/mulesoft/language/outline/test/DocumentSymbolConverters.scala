package org.mulesoft.language.outline.test

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import upickle.default.{macroRW, ReadWriter => RW}

case class DocumentSymbolPosition(line: Int, character: Int)

object DocumentSymbolPosition {

  implicit def rw: RW[DocumentSymbolPosition] = macroRW

  implicit def sharedToTransport(from: Position): DocumentSymbolPosition = {
    DocumentSymbolPosition(from.line, from.column)
  }
}

case class DocumentSymbolRange(start: DocumentSymbolPosition, end: DocumentSymbolPosition)

object DocumentSymbolRange {

  implicit def rw: RW[DocumentSymbolRange] = macroRW

  implicit def sharedToTransport(from: PositionRange): DocumentSymbolRange =
    DocumentSymbolRange(from.start, from.end)
}

case class DocumentSymbolNode(
    name: String,
    kind: Int,
    deprecated: Boolean,
    range: DocumentSymbolRange,
    selectionRange: DocumentSymbolRange,
    children: Seq[DocumentSymbolNode]
)

object DocumentSymbolNode {

  implicit def rw: RW[DocumentSymbolNode] = macroRW

  implicit def sharedToTransport(from: DocumentSymbol): DocumentSymbolNode =
    DocumentSymbolNode(
      from.name,
      from.kind.index,
      from.deprecated,
      from.range,
      from.selectionRange,
      from.children.map(child => DocumentSymbolNode.sharedToTransport(child))
    )
}
