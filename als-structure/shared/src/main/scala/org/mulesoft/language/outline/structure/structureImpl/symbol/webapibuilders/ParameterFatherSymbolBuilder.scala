package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{EndPointModel, ParametersFieldModel}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}

class ArrayParametersSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    implicit val ctx: StructureContext)
    extends ArrayFieldTypeSymbolBuilder {
  override def build(): Seq[DocumentSymbol] = {
    new ParametersSymbolBuilder(value.values.collect({ case p: Parameter => p }),
                                range.map(PositionRange(_)),
                                Some(element.field))
      .build()
      .toSeq
  }

  override protected val optionName: Option[String] = None
}

object HeadersSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {

  override val supportedIri: String = ParametersFieldModel.Headers.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(value, element))
}

object QueryParametersSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ParametersFieldModel.QueryParameters.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(value, element))
}

object QueryStringSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {

  override val supportedIri: String = ParametersFieldModel.QueryString.value.iri()
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(value, element))

}

object UriParametersSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ParametersFieldModel.UriParameters.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(value, element))
}
