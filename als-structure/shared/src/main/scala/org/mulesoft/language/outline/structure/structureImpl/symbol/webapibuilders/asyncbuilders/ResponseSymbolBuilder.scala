package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.ResponseModel
import amf.plugins.domain.webapi.models.Response
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}

class ResponseSymbolBuilder(override val element: Response)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[Response] {

  override def ignoreFields: List[Field]            = super.ignoreFields
  override protected val optionName: Option[String] = Some("message")
}

object ResponseSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[Response] {
  override val supportedIri: String = ResponseModel.`type`.head.iri()

  override def getType: Class[_] = classOf[Response]

  override protected def construct(element: Response)(
      implicit ctx: StructureContext): Option[SymbolBuilder[Response]] =
    Some(new ResponseSymbolBuilder(element))
}
