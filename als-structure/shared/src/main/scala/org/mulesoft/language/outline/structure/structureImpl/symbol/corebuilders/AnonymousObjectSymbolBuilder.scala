package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.AmfObjectSymbolBuilder

case class AnonymousObjectSymbolBuilder(override val element: AmfObject)(override implicit val ctx: StructureContext)
    extends AnonymousObjectSymbolBuilderTrait[AmfObject] {}

trait AnonymousObjectSymbolBuilderTrait[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {
  override protected val optionName: Option[String] = None
}
