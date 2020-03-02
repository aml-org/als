package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.annotations.{BasePathLexicalInformation, HostLexicalInformation}
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

class Oas20WebApiSymbolBuilder(element: WebApi)(override implicit val factory: BuilderFactory)
    extends WebApiSymbolBuilder(element) {

  override protected def buildServerSymbols(v: Value): Seq[DocumentSymbol] = OasBaseUrlSymbolBuilder(v).build()
}

case class OasBaseUrlSymbolBuilder(value: Value) {
  def build(): Seq[DocumentSymbol] = {
    val basePath = value.annotations.find(classOf[BasePathLexicalInformation]).map { a =>
      val range = PositionRange(a.range)
      DocumentSymbol("basePath", SymbolKind.String, false, range, range, Nil)
    }

    val host = value.annotations.find(classOf[HostLexicalInformation]).map { a =>
      val range = PositionRange(a.range)
      DocumentSymbol("host", SymbolKind.String, false, range, range, Nil)
    }

    (basePath ++ host).toSeq
  }
}

object Oas20WebApiSymbolBuilder extends WebApiSymbolBuilderTrait {
  override def construct(element: WebApi)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: WebApi]] =
    Some(new Oas20WebApiSymbolBuilder(element))
}
