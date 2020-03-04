package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfArray
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilder,
  SymbolKind
}

class ParametersSymbolBuilder(parameters: Seq[Parameter], range: PositionRange, selectionRange: PositionRange)(
    implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {
  val children: Seq[DocumentSymbol] =
    parameters.flatMap(e => factory.builderForElement(e).map(_.build()).getOrElse(Nil))

  override def build(): Seq[DocumentSymbol] = {
    parameters.headOption.map { p =>
      DocumentSymbol(ParameterBindingLabelMapper.toLabel(p.binding.value()),
                     SymbolKind.Array,
                     deprecated = false,
                     range,
                     selectionRange,
                     children.toList)
    }.toList
  }
}
