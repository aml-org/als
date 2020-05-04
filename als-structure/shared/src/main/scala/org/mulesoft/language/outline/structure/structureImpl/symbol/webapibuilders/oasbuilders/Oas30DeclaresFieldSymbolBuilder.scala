package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.DeclaresFieldSymbolBuilder

class Oas30DeclaresFieldSymbolBuilder(value: AmfArray, element: FieldEntry)(
    override implicit val factory: BuilderFactory)
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
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new Oas30DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
