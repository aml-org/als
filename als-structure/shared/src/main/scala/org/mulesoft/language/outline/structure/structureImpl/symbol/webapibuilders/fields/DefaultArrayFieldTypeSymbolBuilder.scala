package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.NodeShapeModel
import amf.plugins.domain.webapi.metamodel._
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedArrayFieldSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ParameterBindingLabelMapper

class DefaultArrayFieldTypeSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends NamedArrayFieldSymbolBuilder {

  private val mapNames = Map(
    WebApiModel.Security      -> "Security",
    OperationModel.Tags       -> "tags",
    OperationModel.Responses  -> "responses",
    EncodingModel.Headers     -> ParameterBindingLabelMapper.toLabel("header"),
    NodeShapeModel.Properties -> "properties",
    ShapeModel.Values         -> "enum",
    ResponseModel.Payloads    -> "Payloads",
    RequestModel.Payloads     -> "Payloads"
  )

  override protected val name: String = mapNames.getOrElse(element.field, element.field.value.name)
}

object DefaultArrayFieldTypeSymbolBuilderCompanion extends ArrayFieldTypeSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DefaultArrayFieldTypeSymbolBuilder(value, element))
}
