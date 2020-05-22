package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSymbolBuilder,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class ParameterSchemaFieldBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends ObjectFieldTypeSymbolBuilder {

  private val inner = new AmfObjectSymbolBuilder[AmfObject] {
    override val element: AmfObject             = value
    override implicit val ctx: StructureContext = ctx

    override protected val optionName: Option[String] = None
  }
  override def build(): Seq[DocumentSymbol] = inner.build()

  override protected val optionName: Option[String]     = None
  override protected val children: List[DocumentSymbol] = Nil
}

object ParameterSchemaFieldBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ParameterModel.Schema.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfObject]] = {
    if (element.field == ParameterModel.Schema) Some(new ParameterSchemaFieldBuilder(value, element))
    else None
  }
}
