package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.annotations.{BasePathLexicalInformation, HostLexicalInformation}
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{ServerModel, WebApiModel}
import amf.plugins.domain.webapi.models.Server
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}

object OasBaseUrlFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = WebApiModel.Servers.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new OasBaseUrlFieldSymbolBuilder(value, element))
}

class OasBaseUrlFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends ArrayFieldTypeSymbolBuilder {
  private val default = value.values.collectFirst({ case s: Server => s })
  override def build(): Seq[DocumentSymbol] = {
    default
      .flatMap(_.fields.getValueAsOption(ServerModel.Url))
      .map { value =>
        val basePath = value.annotations.find(classOf[BasePathLexicalInformation]).map { a =>
          val range = PositionRange(a.range)
          DocumentSymbol("basePath",
                         KindForResultMatcher.kindForField(ServerModel.Url),
                         deprecated = false,
                         range,
                         range,
                         Nil)
        }

        val host = value.annotations.find(classOf[HostLexicalInformation]).map { a =>
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

  override protected val optionName: Option[String] = None
}
