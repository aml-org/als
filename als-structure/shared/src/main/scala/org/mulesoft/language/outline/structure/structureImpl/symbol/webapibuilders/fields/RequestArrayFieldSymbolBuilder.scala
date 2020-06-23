package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfArray
import amf.core.parser.{FieldEntry, Range}
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.Request
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

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
