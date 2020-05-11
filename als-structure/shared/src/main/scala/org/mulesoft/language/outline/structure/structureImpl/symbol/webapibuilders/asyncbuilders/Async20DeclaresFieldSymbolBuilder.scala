package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.models.Parameter
import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DeclaresFieldSymbolBuilder

class Async20DeclaresFieldSymbolBuilder(value: AmfArray, element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DeclaresFieldSymbolBuilder(value, element) {
  override protected def declarationName(obj: AmfObject): String = {
    obj match {
      case p: Parameter if p.binding.option().contains("header") => "headers"
      case _: NodeShape                                          => "schemas"
      case _: SecurityScheme                                     => "securitySchemes"
      case _                                                     => super.declarationName(obj)
    }
  }
}

object Async20DeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new Async20DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
