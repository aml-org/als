package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.eventedbuilders

import amf.apicontract.client.scala.model.domain.{Parameter, Request}
import amf.core.client.scala.model.domain.{AmfArray, AmfObject}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.Oas20DeclaresFieldSymbolBuilder

class EventedDeclaresFieldSymbolBuilder(value: AmfArray, element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends Oas20DeclaresFieldSymbolBuilder(value, element) {
  override protected def declarationName(obj: AmfObject): String = {
    obj match {
      case p: Parameter if p.binding.option().contains("header") => "headers"
      case _                                                     => super.declarationName(obj)
    }
  }

  override protected def builderFor(obj: AmfObject): Option[SymbolBuilder[_]] = obj match {
    case r: Request => Some(new NamedElementSymbolBuilder(r))
    case _          => super.builderFor(obj)
  }
}

object EventedDeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new EventedDeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
