package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.apicontract.client.scala.model.domain.security.SecurityScheme
import amf.core.client.scala.model.domain.{AmfArray, AmfObject}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DeclaresFieldSymbolBuilder

class Oas20DeclaresFieldSymbolBuilder(value: AmfArray, element: FieldEntry)(override implicit val ctx: StructureContext)
    extends DeclaresFieldSymbolBuilder(value, element) {
  override protected def declarationName(obj: AmfObject): String = {
    obj match {
      case _: SecurityScheme => "securityDefinitions"
      case _                 => super.declarationName(obj)
    }
  }
}

object Oas20DeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new Oas20DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
