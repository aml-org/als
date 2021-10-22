package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.apicontract.client.scala.model.domain.bindings.ServerBindings
import amf.apicontract.internal.metamodel.domain.bindings.ServerBindingsModel
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class ServerBindingsSymbolBuilder(override val element: ServerBindings)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[ServerBindings] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    element.name.option().orElse(Some("bindings"))

  override protected def children: List[DocumentSymbol] = Nil
}

object ServerBindingsSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[ServerBindings] {
  override val supportedIri: String = ServerBindingsModel.`type`.head.iri()

  override def getType: Class[_] = classOf[ServerBindings]

  override protected def construct(element: ServerBindings)(
      implicit ctx: StructureContext): Option[SymbolBuilder[ServerBindings]] =
    Some(new ServerBindingsSymbolBuilder(element))
}
