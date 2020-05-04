package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion
}

class EncodesFieldSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ObjectFieldTypeSymbolBuilder {

  private val inner = AnonymousObjectSymbolBuilder(value)

  override def build(): Seq[DocumentSymbol] = inner.build()
}

object EncodesFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = DocumentModel.Encodes.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new EncodesFieldSymbolBuilder(value, element))
}
