package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.apicontract.client.scala.model.domain.bindings.MessageBindings
import amf.apicontract.internal.metamodel.domain.bindings.MessageBindingsModel
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class MessageBindingsSymbolBuilder(override val element: MessageBindings)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[MessageBindings] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    element.name.option().orElse(Some("bindings"))

  override protected def children: List[DocumentSymbol] = Nil
}

object MessageBindingsSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[MessageBindings] {
  override val supportedIri: String = MessageBindingsModel.`type`.head.iri()

  override def getType: Class[_] = classOf[MessageBindings]

  override protected def construct(element: MessageBindings)(implicit
      ctx: StructureContext
  ): Option[SymbolBuilder[MessageBindings]] =
    Some(new MessageBindingsSymbolBuilder(element))
}
