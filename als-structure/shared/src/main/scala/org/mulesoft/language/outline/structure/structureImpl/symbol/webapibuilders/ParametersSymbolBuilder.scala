package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.{ParametersFieldModel, RequestModel}
import amf.core.internal.metamodel.Field
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, StructureContext}

/** Creates an structure for the parameters category and each parameter. All parameters must be off the same binding
  */
class ParametersSymbolBuilder(parameters: Seq[Parameter], range: Option[PositionRange], field: Option[Field])(implicit
    val ctx: StructureContext
) {
  def build(): List[DocumentSymbol] = {
    parameters
      .groupBy(_.binding.value())
      .map { case (k, parameters) =>
        val children = parameters.flatMap(e => ctx.factory.builderFor(e).map(_.build()).getOrElse(Nil))
        val r        = range.orElse(children.headOption.map(_.range)).getOrElse(EmptyPositionRange)
        DocumentSymbol(
          ParameterBindingLabelMapper.toLabel(k),
          KindForResultMatcher
            .kindForField(field.getOrElse(fieldFromBinding(k))), // all param fields are the same
          r,
          children.toList
        )
      }
      .toList
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
