package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{EndPointModel, PayloadModel}
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.PayloadFieldSymbolBuilder

class AsyncPayloadFieldSymbolBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val ctx: StructureContext)
    extends PayloadFieldSymbolBuilder(element, value) {

  override protected val payloadsLabel = "payload"

  override def range: PositionRange =
    value.values
      .collectFirst { case p: Payload => p }
      .flatMap(_.fields.entry(PayloadModel.Schema))
      .flatMap(_.value.value.annotations.find(classOf[LexicalInformation]))
      .map(l => PositionRange(l.range))
      .getOrElse(PositionRange(amf.core.parser.Range.NONE))
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
