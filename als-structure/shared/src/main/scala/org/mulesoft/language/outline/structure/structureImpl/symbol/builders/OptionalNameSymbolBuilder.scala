package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher}

trait OptionalNameSymbolBuilder[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {
  protected def optionName: Option[String]

  override protected val selectionRange: Option[PositionRange] = range

  override def build(): Seq[DocumentSymbol] = optionName.fold(children)(name => { build(name).toList })
}
