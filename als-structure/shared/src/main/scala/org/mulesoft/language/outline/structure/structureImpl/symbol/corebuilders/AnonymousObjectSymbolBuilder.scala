package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.AmfObjectSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, StructureContext}

case class AnonymousObjectSymbolBuilder(override val element: AmfObject)(override implicit val ctx: StructureContext)
    extends AnonymousObjectSymbolBuilderTrait[AmfObject] {}

trait AnonymousObjectSymbolBuilderTrait[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {
  override protected val optionName: Option[String] = None
}
