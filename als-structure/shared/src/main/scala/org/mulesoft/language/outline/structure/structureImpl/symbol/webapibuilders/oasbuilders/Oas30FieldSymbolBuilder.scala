package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultArrayFieldTypeSymbolBuilder

object ServerArrayFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Servers.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new ServerArrayFieldSymbolBuilder(value, element))
}

class ServerArrayFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultArrayFieldTypeSymbolBuilder(value, element) {
  override def name: String = "servers"

  override protected val children: List[DocumentSymbol] = Nil
}
