package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder

import amf.core.metamodel.Field
import amf.core.metamodel.domain.ShapeModel
import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.NodeShapeModel
import amf.plugins.domain.webapi.metamodel.{EncodingModel, OperationModel, RequestModel, ResponseModel}
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.FieldArrayBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ParameterBindingLabelMapper

class WebApiCustomArrayBuilder(override implicit val factory: BuilderFactory) extends FieldArrayBuilder {

  override protected val ignoreChildren: Seq[Field] = Seq(OperationModel.Tags)

  private val map: Map[Field, String] = Map(
    OperationModel.Tags       -> "tags",
    OperationModel.Responses  -> "responses",
    EncodingModel.Headers     -> ParameterBindingLabelMapper.toLabel("header"),
    NodeShapeModel.Properties -> "properties",
    ShapeModel.Values         -> "enum",
    ResponseModel.Payloads ->"Payloads",
    RequestModel.Payloads -> "Payloads"
  )

  override def name(fe: FieldEntry): String =
    map.getOrElse(fe.field, fe.field.value.name)
}

object WebApiCustomArrayBuilder{
  def apply()(implicit factory: BuilderFactory): WebApiCustomArrayBuilder = new WebApiCustomArrayBuilder()(factory)
}