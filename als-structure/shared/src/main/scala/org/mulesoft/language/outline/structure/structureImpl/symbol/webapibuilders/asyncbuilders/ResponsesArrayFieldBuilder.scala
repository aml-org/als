package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.apicontract.internal.metamodel.domain.OperationModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.DefaultWebApiArrayFieldTypeSymbolBuilder

case class ResponsesArrayFieldBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  override protected val optionName: Option[String] = None
}

object ResponsesArrayFieldCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(ResponsesArrayFieldBuilder(value, element))

  override val supportedIri: String = OperationModel.Responses.value.iri()
}
