package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ResponseModel
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.DefaultArrayFieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}

case class PayloadsArrayFieldBuilder(firstPayload: Payload,
                                     override val value: AmfArray,
                                     override val element: FieldEntry)(override implicit val factory: BuilderFactory)
    extends DefaultArrayFieldTypeSymbolBuilder(value, element) {

  override def range: PositionRange =
    firstPayload.annotations
      .find(classOf[LexicalInformation])
      .map(le => PositionRange(le.range))
      .getOrElse(super.range)
}

object PayloadsArrayFieldBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ResponseModel.Payloads.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    firstPayload(value).map(PayloadsArrayFieldBuilder(_, value, element))
  }

  private def firstPayload(arr: AmfArray): Option[Payload] = {
    arr.values match {
      case (single: Payload) :: Nil => Some(single)
      case _                        => None
    }
  }
}
