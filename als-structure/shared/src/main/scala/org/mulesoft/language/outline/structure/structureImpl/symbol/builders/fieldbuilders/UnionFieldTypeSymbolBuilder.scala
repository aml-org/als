package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.client.scala.model.domain.AmfElement
import amf.shapes.client.scala.model.domain.UnionShape
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilderCompanion

trait UnionFieldTypeSymbolBuilder extends FieldTypeSymbolBuilderCompanion[UnionShape] {
  override def getElementType: Class[_ <: AmfElement] = classOf[UnionShape]
}