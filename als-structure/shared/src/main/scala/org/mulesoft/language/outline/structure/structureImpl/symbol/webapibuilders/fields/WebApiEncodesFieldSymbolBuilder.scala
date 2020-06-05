package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ObjectFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.{
  DefaultNamedScalarTypeSymbolBuilder,
  EncodesFieldSymbolBuilder
}

class WebApiEncodesFieldSymbolBuilder(override val value: WebApi, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends EncodesFieldSymbolBuilder(value, element) {

  val titleField: Seq[DocumentSymbol] = value.fields
    .fields()
    .find(_.field == WebApiModel.Name)
    .map(fe => new DefaultNamedScalarTypeSymbolBuilder(fe.value.value.asInstanceOf[AmfScalar], fe, "title").build())
    .getOrElse(Nil)

  override def build(): Seq[DocumentSymbol] = titleField ++ inner.build()
}

object WebApiEncodesFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = DocumentModel.Encodes.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfObject]] = value match {
    case w: WebApi => Some(new WebApiEncodesFieldSymbolBuilder(w, element))
    case _         => Some(new EncodesFieldSymbolBuilder(value, element))
  }
}
