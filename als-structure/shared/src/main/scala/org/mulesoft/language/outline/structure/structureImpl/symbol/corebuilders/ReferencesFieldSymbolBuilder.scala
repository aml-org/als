package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.ShapeModel
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

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

object ContentTypeIgnoredFieldBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.ContentType.value.iri()
}

object AcceptsIgnoredFieldBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Accepts.value.iri()
}

object ShapeInheritsArrayFieldBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = ShapeModel.Inherits.value.iri()
}
