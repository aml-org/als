package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.document.BaseUnitModel
import amf.core.model.document.BaseUnit
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  AmfObjectSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, StructureContext}

object BaseUnitSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[BaseUnit] {
  override val supportedIri: String = BaseUnitModel.`type`.head.iri()

  override def getType: Class[_] = classOf[BaseUnit]

  override protected def construct(element: BaseUnit)(
      implicit ctx: StructureContext): Option[SymbolBuilder[BaseUnit]] = Some(new BaseUnitSymbolBuilder(element))
}

class BaseUnitSymbolBuilder(override val element: BaseUnit)(override implicit val ctx: StructureContext)
    extends AmfObjectSymbolBuilder[BaseUnit] {
  override def build(): Seq[DocumentSymbol] = children
}
