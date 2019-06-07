package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfElement
import amf.core.parser.{Range => AmfRange}
import amf.plugins.domain.webapi.metamodel.RequestModel
import amf.plugins.domain.webapi.models.Request
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AmfObjSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  ElementSymbolBuilder,
  ElementSymbolBuilderCompanion
}

class RequestSymbolBuilders(override val element: Request)(override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[Request] {
  override protected val name: String = "request"
  override protected val selectionRange: PositionRange = PositionRange(
    element.annotations.find(classOf[LexicalInformation]).map(_.range).getOrElse(AmfRange.NONE))
}

object RequestSymbolBuilders extends ElementSymbolBuilderCompanion {
  override type T = Request

  override def getType: Class[_ <: AmfElement] = classOf[Request]

  override val supportedIri: String = RequestModel.`type`.head.iri()

  override def construct(element: Request)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Request]] =
    Some(new RequestSymbolBuilders(element))
}
