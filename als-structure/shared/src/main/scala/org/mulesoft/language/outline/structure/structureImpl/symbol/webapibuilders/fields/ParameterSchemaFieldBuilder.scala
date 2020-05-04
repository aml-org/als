package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.language.outline.structure.structureImpl.{
  AmfObjectSymbolBuilder,
  BuilderFactory,
  DocumentSymbol,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion
}

class ParameterSchemaFieldBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ObjectFieldTypeSymbolBuilder {

  private val inner = new AmfObjectSymbolBuilder[AmfObject] {
    override val element: AmfObject               = value
    override implicit val factory: BuilderFactory = factory

    override def build(): Seq[DocumentSymbol] = children
  }
  override def build(): Seq[DocumentSymbol] = inner.build()
}

object ParameterSchemaFieldBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ParameterModel.Schema.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] = {
    if (element.field == ParameterModel.Schema) Some(new ParameterSchemaFieldBuilder(value, element))
    else None
  }
}
