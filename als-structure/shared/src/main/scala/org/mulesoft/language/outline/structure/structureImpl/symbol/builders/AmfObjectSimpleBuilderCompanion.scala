package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.annotations.SourceAST
import amf.core.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.core.model.domain.AmfObject
import amf.plugins.document.webapi.annotations.InlineDefinition
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher}
import org.yaml.model.YMapEntry

import scala.collection.immutable

trait AmfObjectSimpleBuilderCompanion[DM <: AmfObject]
    extends SymbolBuilderCompanion[DM]
    with IriSymbolBuilderCompanion {}

trait AmfObjectSymbolBuilder[DM <: AmfObject] extends SymbolBuilder[DM] {
  def ignoreFields =
    List(DomainElementModel.Extends, LinkableElementModel.Target)

  protected val selectionRange: Option[PositionRange]

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
    if (element.annotations.contains(classOf[InlineDefinition])) Nil else elementChildrens

  private def elementChildrens: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap(o =>
        ctx.factory
          .builderFor(o))
      .flatMap(_.build())

  protected final def build(name: String): Seq[DocumentSymbol] = {
    range
      .map { r =>
        Seq(
          DocumentSymbol(name,
                         KindForResultMatcher.getKind(element),
                         deprecated = false,
                         r,
                         selectionRange.getOrElse(r),
                         skipLoneChild(children, name)))
      }
      .getOrElse(children)

  }
  private def skipLoneChild(children: List[DocumentSymbol], name: String): List[DocumentSymbol] =
    if (children.length == 1 && children.head.name == name)
      children.head.children
    else
      children
}
