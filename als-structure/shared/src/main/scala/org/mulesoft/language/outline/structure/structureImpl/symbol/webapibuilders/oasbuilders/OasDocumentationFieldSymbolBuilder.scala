package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultArrayFieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

class OasDocumentationFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends DefaultArrayFieldTypeSymbolBuilder(value, element) {
  override protected val optionName: Option[String] = Some("documentation")

  override def build(): Seq[DocumentSymbol] = super.build()

  override protected def range: Option[AmfPositionRange] = value.values.headOption.flatMap(_.annotations.range())
}

object OasDocumentationFieldSymbolBuilder
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Documentations.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new OasDocumentationFieldSymbolBuilder(value, element))
  }
}
