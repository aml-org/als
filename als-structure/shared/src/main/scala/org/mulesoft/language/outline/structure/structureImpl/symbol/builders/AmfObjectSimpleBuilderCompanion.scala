package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.aml.internal.metamodel.domain.PropertyMappingModel
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.core.internal.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.shapes.internal.annotations.InlineDefinition
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, SymbolKinds}

trait AmfObjectSimpleBuilderCompanion[DM <: AmfObject]
    extends SymbolBuilderCompanion[DM]
    with IriSymbolBuilderCompanion {}

trait AmfObjectSymbolBuilder[DM <: AmfObject] extends SymbolBuilder[DM] {
  def ignoreFields =
    List(
      DomainElementModel.Extends,
      LinkableElementModel.Target,
      PropertyMappingModel.ObjectRange,
      PropertyShapeModel.And,
      PropertyShapeModel.Or,
      PropertyShapeModel.Xone
    )

  override protected val kind: SymbolKinds.SymbolKind = KindForResultMatcher.getKind(element)

  protected val range: Option[AmfPositionRange] =
    element.annotations
      .astElement()
      .flatMap(rangeFromAst)

  override protected def children: List[DocumentSymbol] =
    if (isReference) Nil else elementChildren

  private def isReference =
    element match {
      case e if e.annotations.contains(classOf[InlineDefinition]) => true
      case e: PropertyShape =>
        e.range.annotations.targetName().isDefined // inlined json schemas, don't know how else to identify
      case _ => false
    }

  private def elementChildren: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap(o =>
        ctx.factory
          .builderFor(o)
      )
      .flatMap(_.build())

}
