package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  FieldTypeSymbolBuilderCompanion
}

trait ArrayFieldTypeSymbolBuilder extends FieldTypeSymbolBuilder[AmfArray] {
  protected val children: List[DocumentSymbol] = value.values
    .collect({ case obj: AmfObject => obj })
    .flatMap(o => ctx.factory.builderFor(o).map(_.build()).getOrElse(Nil))
    .toList
}
trait ArrayFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfArray] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfArray]
}

trait DefaultArrayTypeSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion

trait NamedArrayFieldTypeSymbolBuilder extends ArrayFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfArray] {}
