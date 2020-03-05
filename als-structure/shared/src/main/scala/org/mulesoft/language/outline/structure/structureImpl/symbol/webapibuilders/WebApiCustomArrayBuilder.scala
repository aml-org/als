package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.Field
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{EncodingModel, OperationModel}
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.FieldArrayBuilder

case class WebApiCustomArrayBuilder(override implicit val factory: BuilderFactory) extends FieldArrayBuilder {

  override protected val ignoreChildren: Seq[Field] = Seq(OperationModel.Tags)

  private val map: Map[Field, String] = Map(
    OperationModel.Tags      -> "tags",
    OperationModel.Responses -> "responses",
    EncodingModel.Headers    -> ParameterBindingLabelMapper.toLabel("header")
  )

  override def name(fe: FieldEntry): String =
    map.getOrElse(fe.field, fe.field.value.name)
}
