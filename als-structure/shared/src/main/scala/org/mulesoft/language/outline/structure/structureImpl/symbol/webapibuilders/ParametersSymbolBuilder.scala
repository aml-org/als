package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfArray
import amf.plugins.domain.webapi.metamodel.{OperationModel, ParametersFieldModel, RequestModel}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilder,
  KindForResultMatcher,
  SymbolKind
}

class ParametersSymbolBuilder(parameters: Seq[Parameter], range: PositionRange, selectionRange: PositionRange)(
    implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {
  val children: Seq[DocumentSymbol] =
    parameters.flatMap(e => factory.builderForElement(e).map(_.build()).getOrElse(Nil))

  override def build(): Seq[DocumentSymbol] = {
    parameters.headOption.map { p =>
      DocumentSymbol(
        ParameterBindingLabelMapper.toLabel(p.binding.value()),
        KindForResultMatcher.kindForField(fieldFromBinding(p.binding.value())), // all param fields are the same
        deprecated = false,
        range,
        selectionRange,
        children.toList
      )
    }.toList
  }

  private def fieldFromBinding(binding: String) = {
    binding match {
      case "header" => ParametersFieldModel.Headers
      case "body"   => RequestModel.Payloads // should never match this one
      case "path"   => ParametersFieldModel.UriParameters
      case _        => ParametersFieldModel.QueryParameters
    }
  }
}
