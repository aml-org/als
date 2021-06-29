package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.Server
import amf.apicontract.internal.metamodel.domain.ServerModel
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}

class ServerSymbolBuilder(override val element: Server)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[Server] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    element.name.option()
}

object ServerSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[Server] {
  override val supportedIri: String = ServerModel.`type`.head.iri()

  override def getType: Class[_] = classOf[Server]

  override protected def construct(element: Server)(implicit ctx: StructureContext): Option[SymbolBuilder[Server]] =
    Some(new ServerSymbolBuilder(element))
}
