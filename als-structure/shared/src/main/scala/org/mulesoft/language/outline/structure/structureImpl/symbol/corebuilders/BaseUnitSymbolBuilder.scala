package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.metamodel.document.BaseUnitModel
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  AmfObjectSymbolBuilder,
  SymbolBuilder
}

object BaseUnitSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[BaseUnit] {
  override val supportedIri: String = BaseUnitModel.`type`.head.iri()

  override def getType: Class[_] = classOf[BaseUnit]

  override protected def construct(element: BaseUnit)(
      implicit ctx: StructureContext): Option[SymbolBuilder[BaseUnit]] = Some(new BaseUnitSymbolBuilder(element))
}

class BaseUnitSymbolBuilder(override val element: BaseUnit)(override implicit val ctx: StructureContext)
    extends AmfObjectSymbolBuilder[BaseUnit] {
  override protected val optionName: Option[String] = None
}
