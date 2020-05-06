package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.Request
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion

class RequestArrayFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  private val first = value.values.collectFirst({ case r: Request => r })

  override protected val children: List[DocumentSymbol] =
    first.flatMap(o => ctx.factory.builderFor(o).map(_.build())).getOrElse(Nil).toList

  override protected def range: PositionRange =
    PositionRange(
      first
        .flatMap(_.annotations
          .find(classOf[LexicalInformation]))
        .map(l => l.range)
        .getOrElse(amf.core.parser.Range.NONE))
}

object RequestArrayFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new RequestArrayFieldSymbolBuilder(value, element))
  }

  override val supportedIri: String = OperationModel.Request.value.iri()
}
