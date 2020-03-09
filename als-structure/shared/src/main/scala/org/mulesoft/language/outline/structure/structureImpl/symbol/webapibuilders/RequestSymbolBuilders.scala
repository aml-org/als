package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfElement
import amf.plugins.domain.webapi.metamodel.RequestModel
import amf.plugins.domain.webapi.models.Request
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl._

class RequestSymbolBuilders(override val element: Request)(override implicit val factory: BuilderFactory)
    extends ParamPayloadDecomposeSymbolBuilders[Request](RequestModel.Payloads) {
  override protected val name: String = "Request"

  override def range: Option[PositionRange] = super.range.orElse(Some(EmptyPositionRange))
}

object RequestSymbolBuilders extends ElementSymbolBuilderCompanion {
  override type T = Request

  override def getType: Class[_ <: AmfElement] = classOf[Request]

  override val supportedIri: String = RequestModel.`type`.head.iri()

  override def construct(element: Request)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Request]] =
    Some(new RequestSymbolBuilders(element))
}
