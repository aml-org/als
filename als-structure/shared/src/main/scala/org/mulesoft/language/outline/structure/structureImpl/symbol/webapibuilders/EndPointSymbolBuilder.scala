package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.EndPointModel
import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, RangesSplitter}
class EndPointSymbolBuilder(override val element: EndPoint)(override implicit val factory: BuilderFactory)
    extends ParamPayloadDecomposeSymbolBuilders[EndPoint](EndPointModel.Payloads)
    with ExtendsFatherSymbolBuilder[EndPoint] {

  override def ignoreFields: List[Field] = super.ignoreFields :+ EndPointModel.Parameters
  override protected val name: String =
    element.path.value().stripPrefix(element.parent.flatMap(_.path.option()).getOrElse(""))

  override protected val selectionRange: Option[PositionRange] =
    element.path.annotations().find(classOf[LexicalInformation]).map(l => PositionRange(l.range)).orElse(range)

  override def children: List[DocumentSymbol] = (super.children ++ parametersSymbols).toList

  private val parametersSymbols: List[DocumentSymbol] = {
    val ranges = element.fields
      .getValueAsOption(EndPointModel.Parameters)
      .map(v => RangesSplitter(v.annotations))
      .getOrElse(
        RangesSplitter.Ranges(range.getOrElse(EmptyPositionRange), selectionRange.getOrElse(EmptyPositionRange)))

    element.parameters
      .groupBy(_.binding.value())
      .values
      .flatMap(p => new ParametersSymbolBuilder(p, ranges.range, ranges.selectionRange).build())
      .toList
  }
}
