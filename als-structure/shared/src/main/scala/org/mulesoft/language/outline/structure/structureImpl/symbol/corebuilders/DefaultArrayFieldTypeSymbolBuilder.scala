package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  DefaultArrayTypeSymbolBuilder,
  NamedArrayFieldTypeSymbolBuilder
}
import org.yaml.model.YMapEntry

class DefaultArrayFieldTypeSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends NamedArrayFieldTypeSymbolBuilder {
  override protected def name: String = element.field.value.name.trim match {
    case "" => // should not show empty names
      element.value.annotations
        .ast()
        .collectFirst {
          case entry: YMapEntry => entry.key.as[String]
        }
        .getOrElse("") // if any such case appears, an exception must be added
    case name => name
  }
}

object DefaultArrayFieldTypeSymbolBuilderCompanion extends DefaultArrayTypeSymbolBuilder {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DefaultArrayFieldTypeSymbolBuilder(value, element))
}
