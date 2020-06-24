package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfArray
import amf.core.parser.{FieldEntry, Range}
import amf.plugins.domain.webapi.metamodel.ResponseModel
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
case class PayloadsArrayFieldBuilder(firstPayload: Payload,
                                     override val value: AmfArray,
                                     override val element: FieldEntry)(override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  override protected def range: Option[Range] =
    firstPayload.annotations.range().orElse(super.range)
}

object PayloadsArrayFieldBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ResponseModel.Payloads.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    firstPayload(value).map(PayloadsArrayFieldBuilder(_, value, element))
  }

  private def firstPayload(arr: AmfArray): Option[Payload] = {
    arr.values.headOption match {
      case Some(single: Payload) if arr.values.size == 1 => Some(single)
      case _                                             => None
    }
  }
}
