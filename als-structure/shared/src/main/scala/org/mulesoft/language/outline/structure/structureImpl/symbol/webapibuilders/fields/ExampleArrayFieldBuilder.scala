package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.metamodel.common.ExamplesField
import amf.plugins.domain.shapes.models.Example
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}

case class ExampleArrayFieldBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  override protected val name: String = if (isSingleExample(element)) "example" else "examples"

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
