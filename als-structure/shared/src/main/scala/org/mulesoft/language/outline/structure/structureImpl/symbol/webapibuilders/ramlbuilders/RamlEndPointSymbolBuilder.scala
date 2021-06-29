package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.apicontract.client.scala.model.domain.EndPoint
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.EndPointSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

case class RamlEndPointSymbolBuilder(actual: EndPoint, all: Seq[EndPoint])(override implicit val ctx: StructureContext)
    extends EndPointSymbolBuilder(actual)(ctx) {

  override protected val children: List[DocumentSymbol] =
    super.children ++ all
      .collect({ case e: EndPoint if e.parent.contains(actual) => RamlEndPointSymbolBuilder(e, all)(ctx) })
      .flatMap(_.build())
}
