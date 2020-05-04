package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.AmfObject
import org.mulesoft.language.outline.structure.structureImpl.{AmfObjectSymbolBuilder, BuilderFactory, DocumentSymbol}

case class AnonymousObjectSymbolBuilder(override val element: AmfObject)(override implicit val factory: BuilderFactory)
    extends AmfObjectSymbolBuilder[AmfObject] {
  override def build(): Seq[DocumentSymbol] = children
}
