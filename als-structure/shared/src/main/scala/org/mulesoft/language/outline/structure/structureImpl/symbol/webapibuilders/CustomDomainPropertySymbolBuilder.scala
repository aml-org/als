package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.model.domain.AmfElement
import amf.core.model.domain.extensions.CustomDomainProperty
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait
import org.mulesoft.language.outline.structure.structureImpl.{
  AmfObjectSimpleBuilderCompanion,
  BuilderFactory,
  DocumentSymbol,
  SymbolBuilder
}

class CustomDomainPropertySymbolBuilder(override val element: CustomDomainProperty)(
    override implicit val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[CustomDomainProperty] {
  override protected def children: List[DocumentSymbol] = Nil
}

object CustomDomainPropertySymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[CustomDomainProperty] {
  override def getType: Class[_ <: AmfElement] = classOf[CustomDomainProperty]

  override val supportedIri: String = CustomDomainPropertyModel.`type`.head.iri()

  override def construct(element: CustomDomainProperty)(
      implicit factory: BuilderFactory): Option[SymbolBuilder[CustomDomainProperty]] =
    Some(new CustomDomainPropertySymbolBuilder(element))
}
