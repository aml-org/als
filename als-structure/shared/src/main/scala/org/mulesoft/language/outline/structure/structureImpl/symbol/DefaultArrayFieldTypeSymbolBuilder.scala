package org.mulesoft.language.outline.structure.structureImpl.symbol

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  DocumentSymbol,
  FieldTypeSymbolBuilder,
  SymbolKind
}

class DefaultArrayFieldTypeSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  private val mapNames = Map(
    WebApiModel.Security -> "Security"
  )
  private val name = mapNames.getOrElse(element.field, element.field.value.name)

  override def build(): Seq[DocumentSymbol] =
    Seq(DocumentSymbol(name, SymbolKind.String, deprecated = false, range, range, Nil))

}

object DefaultArrayFieldTypeSymbolBuilderCompanion extends ArrayFieldTypeSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DefaultArrayFieldTypeSymbolBuilder(value, element))
}
