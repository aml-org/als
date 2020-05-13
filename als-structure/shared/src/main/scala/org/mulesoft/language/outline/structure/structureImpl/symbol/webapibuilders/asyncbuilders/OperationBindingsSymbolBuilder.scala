package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.bindings.OperationBindingsModel
import amf.plugins.domain.webapi.models.bindings.OperationBindings
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class OperationBindingsSymbolBuilder(override val element: OperationBindings)(
    override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[OperationBindings] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    element.name.option().orElse(Some("bindings"))

  override protected def children: List[DocumentSymbol] = Nil
}

object OperationBindingsSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[OperationBindings] {
  override val supportedIri: String = OperationBindingsModel.`type`.head.iri()

  override def getType: Class[_] = classOf[OperationBindings]

  override protected def construct(element: OperationBindings)(
      implicit ctx: StructureContext): Option[SymbolBuilder[OperationBindings]] =
    Some(new OperationBindingsSymbolBuilder(element))
}
