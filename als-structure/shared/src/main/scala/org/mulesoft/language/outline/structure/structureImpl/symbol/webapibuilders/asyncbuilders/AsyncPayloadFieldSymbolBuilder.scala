package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.{FieldEntry, Range}
import amf.plugins.domain.webapi.metamodel.{EndPointModel, PayloadModel}
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.amfmanager.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.PayloadFieldSymbolBuilder
class AsyncPayloadFieldSymbolBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val ctx: StructureContext)
    extends PayloadFieldSymbolBuilder(element, value) {

  override protected val payloadsLabel = "payload"

  override protected def range: Option[Range] =
    value.values
      .collectFirst { case p: Payload => p }
      .flatMap(_.fields.entry(PayloadModel.Schema))
      .flatMap(s => s.value.value.annotations.range())
}

object AsyncPayloadFieldSymbolCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri
    : String = EndPointModel.Payloads.value.iri() // same than RequestModel.Payload Apicontract.Payload

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new AsyncPayloadFieldSymbolBuilder(element, value))
}
