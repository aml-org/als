package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.client.scala.model.domain.security.SecurityRequirement
import amf.apicontract.internal.metamodel.domain.security.SecurityRequirementModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class SecurityRequirementFieldSymbolBuilderCompanion(override val element: SecurityRequirement)(
    override implicit val ctx: StructureContext
) extends StructuredSymbolBuilder[SecurityRequirement] {

  override protected val optionName: Option[String] = None

  override protected def children: List[DocumentSymbol] = Nil
}

object SecurityRequirementFieldSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[SecurityRequirement] {
  override val supportedIri: String = SecurityRequirementModel.`type`.head.iri()

  override def getType: Class[_] = classOf[SecurityRequirement]

  override protected def construct(element: SecurityRequirement)(implicit
      ctx: StructureContext
  ): Option[SymbolBuilder[SecurityRequirement]] =
    Some(new SecurityRequirementFieldSymbolBuilderCompanion(element))
}
