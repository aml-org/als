package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.EndPointSymbolBuilder

case class RamlEndPointSymbolBuilder(actual: EndPoint, all: Seq[EndPoint])(
    override implicit val factory: BuilderFactory)
    extends EndPointSymbolBuilder(actual)(factory) {

  override def children: List[DocumentSymbol] =
    super.children ++ all
      .collect({ case e: EndPoint if e.parent.contains(actual) => RamlEndPointSymbolBuilder(e, all)(factory) })
      .flatMap(_.build())
}
