package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.annotations.LexicalInformation
import amf.core.parser.Value
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.{
  WebApiSymbolBuilder,
  WebApiSymbolBuilderTrait
}
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilder,
  SymbolKind
}

class RamlWebApiSymbolBuilder(element: WebApi)(override implicit val factory: BuilderFactory)
    extends WebApiSymbolBuilder(element) {
  override protected def buildServerSymbols(v: Value): Seq[DocumentSymbol] = RamlBaseUrlSymbolBuilder(v).build()
}

case class RamlBaseUrlSymbolBuilder(value: Value) {
  def build(): Seq[DocumentSymbol] = {
    value.annotations
      .find(classOf[LexicalInformation])
      .map { a =>
        val range = PositionRange(a.range)
        DocumentSymbol("baseUri", SymbolKind.String, deprecated = false, range, range, Nil)
      }
      .toSeq
  }
}

object RamlWebApiSymbolBuilder extends WebApiSymbolBuilderTrait {
  override def construct(element: WebApi)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: WebApi]] =
    Some(new RamlWebApiSymbolBuilder(element))
}
