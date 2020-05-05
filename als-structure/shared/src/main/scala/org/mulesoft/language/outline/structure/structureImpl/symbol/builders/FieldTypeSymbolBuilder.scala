package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfElement
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, StructureContext}

trait FieldTypeSymbolBuilder[ElementType <: AmfElement] extends FieldSymbolBuilder {
  val value: ElementType

  protected def range: PositionRange =
    PositionRange(
      element.value.annotations
        .find(classOf[LexicalInformation])
        .map(l => l.range)
        .orElse(value.annotations
          .find(classOf[LexicalInformation])
          .map(l => l.range))
        .getOrElse(amf.core.parser.Range.NONE))
}

trait FieldTypeSymbolBuilderCompanion[ElementType <: AmfElement] extends FieldSymbolBuilderCompanion {

  def getElementType: Class[_ <: AmfElement]
  def construct(element: FieldEntry, value: ElementType)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[ElementType]]

  final override def construct(element: FieldEntry)(
      implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = {
    if (getElementType.isInstance(element.value.value)) {
      construct(element, element.value.value.asInstanceOf[ElementType])
    } else None
  }
}
