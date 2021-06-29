package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.apicontract.client.scala.model.domain.bindings.ChannelBindings
import amf.apicontract.internal.metamodel.domain.bindings.ChannelBindingsModel
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class ChannelBindingsSymbolBuilder(override val element: ChannelBindings)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[ChannelBindings] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    element.name.option.orElse(Some("bindings"))

  override protected def children: List[DocumentSymbol] = Nil
}

object ChannelBindingsSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[ChannelBindings] {
  override val supportedIri: String = ChannelBindingsModel.`type`.head.iri()

  override def getType: Class[_] = classOf[ChannelBindings]

  override protected def construct(element: ChannelBindings)(
      implicit ctx: StructureContext): Option[SymbolBuilder[ChannelBindings]] =
    Some(new ChannelBindingsSymbolBuilder(element))
}
