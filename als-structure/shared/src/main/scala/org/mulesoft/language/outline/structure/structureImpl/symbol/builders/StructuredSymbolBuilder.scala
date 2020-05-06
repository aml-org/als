package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher}

/**
  * Builder for nodes that have structure(name, range, etc) and not should be skipped to show their sons
  *
  * @tparam T
  */
trait StructuredSymbolBuilder[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {

  protected val name: String

  override def build(): Seq[DocumentSymbol] =
    if (name.isEmpty) Nil
    else build(name)
}
