package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.models.Example
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.FieldArrayBuilder

case class ExamplesCustomArrayBuilder(override implicit val factory: BuilderFactory) extends FieldArrayBuilder {
  override def applies(fe: FieldEntry): Boolean =
    fe.value.value.isInstanceOf[AmfArray] && ParameterModel.Examples == fe.field

  override protected def name(fe: FieldEntry): String =
    if (isSingleExample(fe)) "example" else "examples"

  private def isSingleExample(fe: FieldEntry) =
    fe.array.values.exists {
      case head: Example => head.name.isNullOrEmpty && !hasMediaType(head)
      case _             => false
    }

  private def hasMediaType(e: Example): Boolean =
    e.fields.fields().exists(_.field == ExampleModel.MediaType)

}
