package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.metamodel.domain.ShapeModel
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

class ShapeInheritsSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends ArrayFieldTypeSymbolBuilder {
  override protected val children: List[DocumentSymbol] = Nil

  override protected val optionName: Option[String] = None
}

object ShapeInheritsSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ShapeModel.Inherits.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    val builder: ArrayFieldTypeSymbolBuilder = new ShapeInheritsSymbolBuilder(value, element)
    Some(builder)
  }
}
