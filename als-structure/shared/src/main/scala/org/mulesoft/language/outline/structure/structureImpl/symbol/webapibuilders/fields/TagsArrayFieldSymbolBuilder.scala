package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.internal.metamodel.domain.TagsModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class TagsArrayFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {
  override protected val children: List[DocumentSymbol] = Nil
}

object TagsArrayFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new TagsArrayFieldSymbolBuilder(value, element))
  }

  override val supportedIri: String = new TagsModel {}.Tags.value.iri()
}
