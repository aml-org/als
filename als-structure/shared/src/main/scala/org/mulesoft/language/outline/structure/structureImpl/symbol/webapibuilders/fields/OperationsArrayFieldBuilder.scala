package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.internal.metamodel.domain.EndPointModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}

class OperationsArrayFieldBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends ArrayFieldTypeSymbolBuilder {
  override protected val optionName: Option[String] = None
}

object OperationsArrayFieldBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = EndPointModel.Operations.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new OperationsArrayFieldBuilder(value, element))
  }
}
