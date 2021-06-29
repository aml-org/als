package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.AmfScalar
import amf.core.internal.metamodel.Field
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  DefaultScalarTypeSymbolBuilder,
  NamedScalarFieldTypeSymbolBuilder
}

class DefaultNamedScalarTypeSymbolBuilder(override val value: AmfScalar,
                                          override val element: FieldEntry,
                                          override val name: String)(override implicit val ctx: StructureContext)
    extends NamedScalarFieldTypeSymbolBuilder {}

trait DefaultMappedScalarTypeSymbolBuilderCompanion extends DefaultScalarTypeSymbolBuilder {

  protected val mapName: Map[Field, String]

  override def construct(element: FieldEntry, value: AmfScalar)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfScalar]] =
    mapName.get(element.field).map(name => new DefaultNamedScalarTypeSymbolBuilder(value, element, name))
}
