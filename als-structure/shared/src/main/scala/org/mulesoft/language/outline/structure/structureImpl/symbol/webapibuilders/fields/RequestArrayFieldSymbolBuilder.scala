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
import amf.core.parser.Range
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.amfmanager.AmfImplicits.AmfAnnotationsImp
class RequestArrayFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  private val first = value.values.collectFirst({ case r: Request => r })

  override protected val children: List[DocumentSymbol] =
    first.flatMap(o => ctx.factory.builderFor(o).map(_.build())).getOrElse(Nil).toList

  override protected val range: Option[Range] = first.flatMap(_.annotations.range())
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
