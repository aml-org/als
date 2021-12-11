package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.apicontract.internal.metamodel.domain.templates.ResourceTypeModel
import amf.core.client.common.position.{Range => AmfRange}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext, SymbolKinds}

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
  override protected val kind: SymbolKinds.SymbolKind   = SymbolKinds.Property
  override protected val range: Option[AmfRange]        = None
}

object VariableFieldSymbolBuilderCompanion extends IgnoreFieldSymbolBuilderCompanion {
  override val supportedIri: String = ResourceTypeModel.Variables.value.iri()
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
