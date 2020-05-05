package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.domain.extensions.DomainExtensionModel
import amf.core.model.domain.AmfElement
import amf.core.model.domain.extensions.DomainExtension
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, StructureContext}

class DomainExtensionSymbolBuilder(override val element: DomainExtension)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[DomainExtension] {

  override protected val name: String =
    element.name.option().getOrElse(element.id)

  override protected def children: List[DocumentSymbol] = Nil

  override protected val selectionRange: Option[PositionRange] =
    element.annotations
      .find(classOf[LexicalInformation])
      .map(_.range)
      .map(PositionRange.apply)
}

object DomainExtensionSymbolBuilder extends AmfObjectSimpleBuilderCompanion[DomainExtension] {
  override def getType: Class[_ <: AmfElement] = classOf[DomainExtension]

  override val supportedIri: String = DomainExtensionModel.`type`.head.iri()

  override def construct(element: DomainExtension)(
      implicit ctx: StructureContext): Option[SymbolBuilder[DomainExtension]] =
    Some(new DomainExtensionSymbolBuilder(element))
}
