package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.{ParametersFieldModel, RequestModel}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, KindForResultMatcher}

/**
  * Creates an structure for the parameters category and each parameter. All parameters must be off the same binding
  */
class ParametersSymbolBuilder(parameters: Seq[Parameter], range: PositionRange, field: Option[Field])(
    implicit val factory: BuilderFactory) {
  val children: Seq[DocumentSymbol] =
    parameters.flatMap(e => factory.builderFor(e).map(_.build()).getOrElse(Nil))

  def build(): Option[DocumentSymbol] = {
    parameters.headOption.map { p =>
      DocumentSymbol(
        ParameterBindingLabelMapper.toLabel(p.binding.value()),
        KindForResultMatcher
          .kindForField(field.getOrElse(fieldFromBinding(p.binding.value()))), // all param fields are the same
        deprecated = false,
        range,
        range,
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
