package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.aml.internal.metamodel.domain.PropertyMappingModel
import amf.core.client.common.position.Range
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.annotations.SourceAST
import amf.core.internal.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.shapes.internal.annotations.InlineDefinition
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, SymbolKinds}
import org.yaml.model.YMapEntry

trait AmfObjectSimpleBuilderCompanion[DM <: AmfObject]
    extends SymbolBuilderCompanion[DM]
    with IriSymbolBuilderCompanion {}

trait AmfObjectSymbolBuilder[DM <: AmfObject] extends SymbolBuilder[DM] {
  def ignoreFields =
    List(DomainElementModel.Extends, LinkableElementModel.Target, PropertyMappingModel.ObjectRange)

  override protected val kind: SymbolKinds.SymbolKind = KindForResultMatcher.getKind(element)

  protected val range: Option[Range] =
    element.annotations
      .find(classOf[SourceAST])
      .flatMap(_.ast match {
        case yme: YMapEntry if yme.key.sourceName.isEmpty                 => None
        case yme: YMapEntry if yme.value.sourceName != yme.key.sourceName => Some(Range(yme.key.range))
        case y if y.sourceName.isEmpty                                    => None
        case y                                                            => Some(Range(y.range))
      })

  override protected def children: List[DocumentSymbol] =
    if (element.annotations.contains(classOf[InlineDefinition])) Nil else elementChildren

  private def elementChildren: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap(o =>
        ctx.factory
          .builderFor(o))
      .flatMap(_.build())

}
