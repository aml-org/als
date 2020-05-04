package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  ObjectFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.SingleObjectFieldSymbolBuilder

class ContactFieldSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends SingleObjectFieldSymbolBuilder {
  override protected val name: String = "Contact"
}

object ContactFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {

  override val supportedIri: String = WebApiModel.Provider.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new ContactFieldSymbolBuilder(value, element))
}
