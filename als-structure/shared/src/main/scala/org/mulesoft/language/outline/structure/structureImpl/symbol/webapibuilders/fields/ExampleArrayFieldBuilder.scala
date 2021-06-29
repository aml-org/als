package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import amf.core.client.common.position.{Range => AmfRange}
import amf.shapes.client.scala.model.domain.Example
import amf.shapes.internal.domain.metamodel.common.ExamplesField
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

case class ExampleArrayFieldBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  override protected val name: String = if (isSingleExample(element)) "example" else "examples"

  override protected def range: Option[AmfRange] =
    super.range.orElse(value.values.headOption.flatMap(_.annotations.range()))

  private def isSingleExample(fe: FieldEntry) = fe.array.values.exists {
    case head: Example => head.name.isNullOrEmpty && !hasMediaType(head)
    case _             => false
  }

  private def hasMediaType(e: Example): Boolean =
    e.fields.fields().exists(_.field == ExampleModel.MediaType)
}

object ExampleArrayFieldCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(ExampleArrayFieldBuilder(value, element))

  override val supportedIri: String = ExamplesField.Examples.value.iri()
}
