package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfArray
import amf.core.parser.{FieldEntry, Value}
import amf.plugins.domain.webapi.metamodel.{ServerModel, WebApiModel}
import amf.plugins.domain.webapi.models.{Server, WebApi}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  DocumentSymbol,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  KindForResultMatcher,
  SymbolBuilder
}

class RamlBaseUriFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  private val firstServer: Option[Server] = value.values.collectFirst({ case s: Server => s })
  override def build(): Seq[DocumentSymbol] = {
    firstServer.flatMap { s =>
      s.annotations
        .find(classOf[LexicalInformation])
        .map { a =>
          val range = PositionRange(a.range)
          DocumentSymbol("baseUri",
                         KindForResultMatcher.kindForField(ServerModel.Url),
                         deprecated = false,
                         range,
                         range,
                         Nil)
        }
    }.toSeq
  }
}

object RamlBaseUriFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new RamlBaseUriFieldSymbolBuilder(value, element))

  override val supportedIri: String = WebApiModel.Servers.value.iri()
}
