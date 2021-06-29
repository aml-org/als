package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.client.scala.model.domain.api.Api
import amf.apicontract.internal.metamodel.domain.api.{AsyncApiModel, WebApiModel}
import amf.core.client.scala.model.domain.{AmfObject, AmfScalar}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.domain.FieldEntry
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

class ApiEncodesFieldSymbolBuilder(override val value: Api, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends EncodesFieldSymbolBuilder(value, element) {

  private val webApiTitle: Option[FieldEntry] = value.fields
    .fields()
    .find(_.field == WebApiModel.Name)

  private val asyncApiTitle: Option[FieldEntry] = value.fields
    .fields()
    .find(_.field == AsyncApiModel.Name)

  val titleField: Seq[DocumentSymbol] = webApiTitle
    .orElse(asyncApiTitle)
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
    case w: Api => Some(new ApiEncodesFieldSymbolBuilder(w, element))
    case _      => Some(new EncodesFieldSymbolBuilder(value, element))
  }
}
