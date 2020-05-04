package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder

import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedArrayFieldSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}

case class CustomDomainPropertiesCustomArrayBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val factory: BuilderFactory)
    extends NamedArrayFieldSymbolBuilder {

  override protected val name: String = "Extensions"
}

object CustomDomainPropertiesCustomArrayBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = DomainElementModel.CustomDomainProperties.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(CustomDomainPropertiesCustomArrayBuilder(element, value))
}
