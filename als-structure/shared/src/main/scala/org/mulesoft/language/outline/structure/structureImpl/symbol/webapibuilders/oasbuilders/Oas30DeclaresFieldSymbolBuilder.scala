package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, StructureContext}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DeclaresFieldSymbolBuilder

class Oas30DeclaresFieldSymbolBuilder(value: AmfArray, element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DeclaresFieldSymbolBuilder(value, element) {
  override protected def declarationName(obj: AmfObject): String = {
    obj match {
      case p: Parameter if p.binding.option().contains("header") => "headers"
      case _                                                     => super.declarationName(obj)
    }
  }
}

object Oas30DeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new Oas30DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
