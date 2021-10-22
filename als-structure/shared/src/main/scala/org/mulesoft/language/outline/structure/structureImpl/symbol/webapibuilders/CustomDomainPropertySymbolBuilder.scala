package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.client.scala.model.domain.AmfElement
import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class CustomDomainPropertySymbolBuilder(override val element: CustomDomainProperty)(
    override implicit val ctx: StructureContext)
    extends NamedElementSymbolBuilderTrait[CustomDomainProperty] {
  override protected val children: List[DocumentSymbol] = Nil
}

object CustomDomainPropertySymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[CustomDomainProperty] {
  override def getType: Class[_ <: AmfElement] = classOf[CustomDomainProperty]

  override val supportedIri: String = CustomDomainPropertyModel.`type`.head.iri()

  override def construct(element: CustomDomainProperty)(
      implicit ctx: StructureContext): Option[SymbolBuilder[CustomDomainProperty]] =
    Some(new CustomDomainPropertySymbolBuilder(element))
}
