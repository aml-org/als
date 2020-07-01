package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.domain.extensions.DomainExtensionModel
import amf.core.model.domain.AmfElement
import amf.core.model.domain.extensions.DomainExtension
import amf.core.parser.Range
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class DomainExtensionSymbolBuilder(override val element: DomainExtension)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[DomainExtension] {

  override protected val optionName: Option[String] =
    element.name.option().orElse(Some(element.id))

  override protected val children: List[DocumentSymbol] = Nil

}

object DomainExtensionSymbolBuilder extends AmfObjectSimpleBuilderCompanion[DomainExtension] {
  override def getType: Class[_ <: AmfElement] = classOf[DomainExtension]

  override val supportedIri: String = DomainExtensionModel.`type`.head.iri()

  override def construct(element: DomainExtension)(
      implicit ctx: StructureContext): Option[SymbolBuilder[DomainExtension]] =
    Some(new DomainExtensionSymbolBuilder(element))
}
