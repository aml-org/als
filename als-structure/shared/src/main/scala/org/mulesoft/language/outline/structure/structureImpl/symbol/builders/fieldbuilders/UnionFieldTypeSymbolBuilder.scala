package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.model.domain.AmfElement
import amf.plugins.domain.shapes.models.UnionShape
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilderCompanion

trait UnionFieldTypeSymbolBuilder extends FieldTypeSymbolBuilderCompanion[UnionShape] {
  override def getElementType: Class[_ <: AmfElement] = classOf[UnionShape]
}