package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.client.scala.model.domain.Request
import amf.apicontract.internal.metamodel.domain.OperationModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}
import amf.core.client.common.position.{Range => AmfRange}

class RequestArrayFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  private val first = value.values.collectFirst({ case r: Request => r })

  override protected val children: List[DocumentSymbol] =
    first.flatMap(o => ctx.factory.builderFor(o).map(_.build())).getOrElse(Nil).toList

  override protected val range: Option[AmfRange] =
    first.flatMap(_.annotations.ast()).flatMap(rangeFromAst)
}

object RequestArrayFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new RequestArrayFieldSymbolBuilder(value, element))
  }

  override val supportedIri: String = OperationModel.Request.value.iri()
}
