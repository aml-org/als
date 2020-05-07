package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{ServerModel, WebApiModel}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilderCompanion,
  NamedArrayFieldTypeSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{
  DocumentSymbol,
  KindForResultMatcher,
  StructureContext,
  SymbolKind
}

class RamlBaseUriFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends NamedArrayFieldTypeSymbolBuilder {

  override protected val kind: SymbolKind.SymbolKind = KindForResultMatcher.kindForField(ServerModel.Url)
  override def build(): Seq[DocumentSymbol]          = if (value.values.nonEmpty) super.build() else Nil

  override protected val children: List[DocumentSymbol] = Nil
  override protected val name: String                   = "baseUri"
}

object RamlBaseUriFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new RamlBaseUriFieldSymbolBuilder(value, element))

  override val supportedIri: String = WebApiModel.Servers.value.iri()
}
