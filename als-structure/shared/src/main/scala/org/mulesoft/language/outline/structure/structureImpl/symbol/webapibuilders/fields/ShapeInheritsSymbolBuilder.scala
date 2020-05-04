package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  DocumentSymbol,
  FieldSymbolBuilder,
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedArrayFieldSymbolBuilder

class ShapeInheritsSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends NamedArrayFieldSymbolBuilder {
  override protected val children: List[DocumentSymbol] = Nil
  override protected val name: String                   = "inherits"
}

object ShapeInheritsSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String = ShapeModel.Inherits.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    val builder: ArrayFieldTypeSymbolBuilder = new ShapeInheritsSymbolBuilder(value, element)
    Some(builder)
  }
}
