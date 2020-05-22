package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.bindings.ServerBindingsModel
import amf.plugins.domain.webapi.models.bindings.ServerBindings
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
