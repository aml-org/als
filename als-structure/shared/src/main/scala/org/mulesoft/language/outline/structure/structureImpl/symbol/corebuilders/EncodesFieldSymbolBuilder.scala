package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class EncodesFieldSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends ObjectFieldTypeSymbolBuilder {

  protected val inner: AnonymousObjectSymbolBuilder = AnonymousObjectSymbolBuilder(value)

  override def build(): Seq[DocumentSymbol] = inner.build()

  override protected val optionName: Option[String]     = None
  override protected val children: List[DocumentSymbol] = Nil
}

object EncodesFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = DocumentModel.Encodes.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new EncodesFieldSymbolBuilder(value, element))
}
