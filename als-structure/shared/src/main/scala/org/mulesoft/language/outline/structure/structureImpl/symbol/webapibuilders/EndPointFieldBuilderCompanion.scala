package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.EndPoint
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.RamlEndPointSymbolBuilder

object EndPointFieldBuilderCompanion extends IriFieldSymbolBuilderCompanion with ArrayFieldTypeSymbolBuilderCompanion {

  override val supportedIri: String = WebApiModel.EndPoints.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new EndPointFieldSymbolBuilder(value, element))
}

class EndPointFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends ArrayFieldTypeSymbolBuilder {

  override def build(): Seq[DocumentSymbol] = {

    val endpoints = value.values.collect({ case e: EndPoint => e })
    endpoints
      .collect({
        case e: EndPoint if e.parent.isEmpty =>
          RamlEndPointSymbolBuilder(e, endpoints)(ctx)
      })
      .flatMap(_.build())
  }

  override protected val optionName: Option[String] = None
}
