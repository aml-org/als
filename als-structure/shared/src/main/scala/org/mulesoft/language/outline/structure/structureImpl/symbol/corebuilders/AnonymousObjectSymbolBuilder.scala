package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.AmfObject
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.AmfObjectSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, StructureContext}

case class AnonymousObjectSymbolBuilder(override val element: AmfObject)(override implicit val ctx: StructureContext)
    extends AmfObjectSymbolBuilder[AmfObject] {
  override def build(): Seq[DocumentSymbol] = children
}
