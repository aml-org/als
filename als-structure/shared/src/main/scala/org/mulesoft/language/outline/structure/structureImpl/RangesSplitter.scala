package org.mulesoft.language.outline.structure.structureImpl

import amf.core.annotations.SourceAST
import amf.core.parser.Annotations
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lexer.InputRange
import org.yaml.model.YMapEntry

object RangesSplitter {
  case class Ranges(range: PositionRange, selectionRange: PositionRange)

  def apply(annotations: Annotations): Ranges = {
    val (key, range) = annotations.find(classOf[SourceAST]).map(_.ast) match {
      case Some(e: YMapEntry) => (e.key.range, e.range)
      case Some(other)        => (other.range, other.range)
      case _                  => (InputRange.Zero, InputRange.Zero)
    }
    Ranges(PositionRange(range), PositionRange(key))
  }

}
