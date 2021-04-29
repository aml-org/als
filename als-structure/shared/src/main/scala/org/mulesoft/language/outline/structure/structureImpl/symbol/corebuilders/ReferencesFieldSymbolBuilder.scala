package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.templates.AbstractDeclarationModel
import amf.core.parser
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.api.WebApiModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext, SymbolKind}

trait IgnoreFieldSymbolBuilderCompanion extends IriFieldSymbolBuilderCompanion {
  override protected def construct(element: FieldEntry)(
      implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = Some(IgnoreFieldSymbolBuilder)

}

object IgnoreFieldSymbolBuilder extends FieldSymbolBuilder {
  override val element: FieldEntry          = null
  override val ctx: StructureContext        = null
  override def build(): Seq[DocumentSymbol] = Nil

  override protected val optionName: Option[String]     = None
  override protected val children: List[DocumentSymbol] = Nil
  override protected val kind: SymbolKind.SymbolKind    = SymbolKind.Property
  override protected val range: Option[parser.Range]    = None
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

object ProtocolsArrayFieldBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Schemes.value.iri()
}

object AbstractDeclarationDataNodeBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = AbstractDeclarationModel.DataNode.value.iri()
}
