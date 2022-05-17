package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.apicontract.client.scala.model.domain.Payload
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ParametersFieldModel, PayloadModel}
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.PayloadFieldSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, StructureContext}
import amf.core.client.common.position.{Range => AmfRange}

class AsyncPayloadFieldSymbolBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val ctx: StructureContext
) extends PayloadFieldSymbolBuilder(element, value) {

  override protected def buildForKey(key: String, sons: List[DocumentSymbol]): Option[DocumentSymbol] =
    range
      .map(PositionRange.apply)
      .map(r =>
        DocumentSymbol(
          key,
          KindForResultMatcher.kindForField(ParametersFieldModel.QueryParameters),
          r,
          skipLoneChild(sons, key)
        )
      )

  override protected val payloadsLabel      = "payload"
  override def build(): Seq[DocumentSymbol] = payloadSymbols.toSeq
  override protected def range: Option[AmfRange] =
    value.values
      .collectFirst { case p: Payload => p }
      .flatMap(_.fields.entry(PayloadModel.Schema))
      .flatMap(s => s.value.value.annotations.range())
}

object AsyncPayloadFieldSymbolCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String =
    EndPointModel.Payloads.value.iri() // same as RequestModel.Payload APIContract.Payload

  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new AsyncPayloadFieldSymbolBuilder(element, value))
}
