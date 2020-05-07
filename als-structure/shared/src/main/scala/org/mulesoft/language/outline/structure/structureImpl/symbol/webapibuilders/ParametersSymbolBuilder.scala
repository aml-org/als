package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.{ParametersFieldModel, RequestModel}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, StructureContext}

/**
  * Creates an structure for the parameters category and each parameter. All parameters must be off the same binding
  */
class ParametersSymbolBuilder(parameters: Seq[Parameter], range: Option[PositionRange], field: Option[Field])(
    implicit val ctx: StructureContext) {
  val children: Seq[DocumentSymbol] =
    parameters.flatMap(e => ctx.factory.builderFor(e).map(_.build()).getOrElse(Nil))
  private val r = range.orElse(children.headOption.map(_.range)).getOrElse(EmptyPositionRange)
  def build(): Option[DocumentSymbol] = {
    parameters.headOption.map { p =>
      DocumentSymbol(
        ParameterBindingLabelMapper.toLabel(p.binding.value()),
        KindForResultMatcher
          .kindForField(field.getOrElse(fieldFromBinding(p.binding.value()))), // all param fields are the same
        deprecated = false,
        r,
        r,
        children.toList
      )
    }
  }

  private def fieldFromBinding(binding: String): Field = {
    binding match {
      case "header" => ParametersFieldModel.Headers
      case "body"   => RequestModel.Payloads // should never match this one
      case "path"   => ParametersFieldModel.UriParameters
      case _        => ParametersFieldModel.QueryParameters
    }
  }
}
