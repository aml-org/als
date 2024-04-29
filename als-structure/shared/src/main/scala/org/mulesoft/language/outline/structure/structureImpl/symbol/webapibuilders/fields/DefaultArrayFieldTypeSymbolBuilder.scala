package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.internal.metamodel.domain.{
  EncodingModel,
  OperationModel,
  RequestModel,
  ResponseModel,
  TemplatedLinkModel
}
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.DefaultArrayTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultArrayFieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ParameterBindingLabelMapper

class DefaultWebApiArrayFieldTypeSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends DefaultArrayFieldTypeSymbolBuilder(value, element) {

  private val mapNames = Map(
    WebApiModel.Security                      -> "Security",
    OperationModel.Tags                       -> "tags",
    OperationModel.Responses                  -> "responses",
    EncodingModel.Headers                     -> ParameterBindingLabelMapper.toLabel("header"),
    NodeShapeModel.Properties                 -> "properties",
    ShapeModel.Values                         -> "enum",
    ResponseModel.Payloads                    -> "Payloads",
    RequestModel.Payloads                     -> "Payloads",
    DomainElementModel.CustomDomainProperties -> "Extensions",
    WebApiModel.Schemes                       -> "protocols",
    OperationModel.Request                    -> "Request",
    ResponseModel.Links                       -> "links",
    TemplatedLinkModel.Mapping                -> "parameters",
    OperationModel.Bindings                   -> "bindings"
  )
  override protected def name: String = mapNames.getOrElse(element.field, super.name)
}

object DefaultWebApiArrayFieldTypeSymbolBuilderCompanion extends DefaultArrayTypeSymbolBuilder {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DefaultWebApiArrayFieldTypeSymbolBuilder(value, element))
}
