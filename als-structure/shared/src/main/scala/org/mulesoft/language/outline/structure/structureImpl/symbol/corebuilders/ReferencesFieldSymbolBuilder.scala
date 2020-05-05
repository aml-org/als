package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion

trait IgnoreFieldSymbolBuilderCompanion extends IriFieldSymbolBuilderCompanion {
  override protected def construct(element: FieldEntry)(
      implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = Some(IgnoreFieldSymbolBuilder)

}

object IgnoreFieldSymbolBuilder extends FieldSymbolBuilder {
  override val element: FieldEntry          = null
  override val ctx: StructureContext        = null
  override def build(): Seq[DocumentSymbol] = Nil
}

object ReferencesFieldSymbolBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = DocumentModel.References.value.iri()
}
