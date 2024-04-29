package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.apicontract.internal.metamodel.domain.ServerModel
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
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
  SymbolKinds
}

class RamlBaseUriFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends NamedArrayFieldTypeSymbolBuilder {

  override protected val kind: SymbolKinds.SymbolKind = KindForResultMatcher.kindForField(ServerModel.Url)
  override def build(): Seq[DocumentSymbol]           = if (value.values.nonEmpty) super.build() else Nil

  override protected val children: List[DocumentSymbol] = Nil
  override protected val name: String                   = "baseUri"
}

object RamlBaseUriFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new RamlBaseUriFieldSymbolBuilder(value, element))

  override val supportedIri: String = WebApiModel.Servers.value.iri()
}
