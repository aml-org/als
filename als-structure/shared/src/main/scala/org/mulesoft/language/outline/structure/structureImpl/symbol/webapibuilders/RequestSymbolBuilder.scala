package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.Request
import amf.apicontract.internal.metamodel.domain.RequestModel
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AnonymousObjectSymbolBuilderTrait

class RequestSymbolBuilder(override val element: Request)(override implicit val ctx: StructureContext)
    extends AnonymousObjectSymbolBuilderTrait[Request]

object RequestSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[Request] {
  override val supportedIri: String = RequestModel.`type`.head.iri()

  override def getType: Class[_] = classOf[Request]

  override protected def construct(element: Request)(implicit ctx: StructureContext): Option[SymbolBuilder[Request]] =
    Some(new RequestSymbolBuilder(element))
}
