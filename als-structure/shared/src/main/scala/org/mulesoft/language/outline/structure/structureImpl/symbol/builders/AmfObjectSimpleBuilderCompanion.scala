package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.annotations.SourceAST
import amf.core.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.yaml.model.YMapEntry

trait AmfObjectSimpleBuilderCompanion[DM <: AmfObject]
    extends SymbolBuilderCompanion[DM]
    with IriSymbolBuilderCompanion {}

trait AmfObjectSymbolBuilder[DM <: AmfObject] extends SymbolBuilder[DM] {
  def ignoreFields =
    List(DomainElementModel.Extends, LinkableElementModel.Target)

  protected def range: Option[PositionRange] =
    element.annotations
      .find(classOf[SourceAST])
      .flatMap(_.ast match {
        case yme: YMapEntry if yme.key.sourceName.isEmpty => None
        case yme: YMapEntry if yme.value.sourceName != yme.key.sourceName =>
          Some(PositionRange(yme.key.range))
        case y if y.sourceName.isEmpty => None
        case y                         => Some(PositionRange(y.range))
      })

  protected def children: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap(o =>
        ctx.factory
          .builderFor(o))
      .flatMap(_.build())
}
