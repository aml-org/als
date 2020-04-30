package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.annotations.{BasePathLexicalInformation, HostLexicalInformation}
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{ServerModel, WebApiModel}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._

object OasBaseUrlSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Servers.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new OasBaseUrlSymbolBuilder(value, element))
}

class OasBaseUrlSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {
  private val default = value.values.headOption
  def build(): Seq[DocumentSymbol] = {
    default
      .map { server =>
        val basePath = server.annotations.find(classOf[BasePathLexicalInformation]).map { a =>
          val range = PositionRange(a.range)
          DocumentSymbol("basePath",
                         KindForResultMatcher.kindForField(ServerModel.Url),
                         deprecated = false,
                         range,
                         range,
                         Nil)
        }

        val host = server.annotations.find(classOf[HostLexicalInformation]).map { a =>
          val range = PositionRange(a.range)
          DocumentSymbol("host",
                         KindForResultMatcher.kindForField(ServerModel.Url),
                         deprecated = false,
                         range,
                         range,
                         Nil)
        }

        (basePath ++ host).toSeq
      }
      .getOrElse(Nil)
  }
}
