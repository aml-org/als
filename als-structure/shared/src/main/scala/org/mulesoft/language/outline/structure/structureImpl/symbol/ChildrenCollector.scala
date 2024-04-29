package org.mulesoft.language.outline.structure.structureImpl.symbol

import amf.core.client.scala.model.domain.{AmfArray, AmfObject}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

trait ChildrenCollector {
  protected def collectChildren(value: AmfArray)(implicit ctx: StructureContext): List[DocumentSymbol] =
    value.values
      .collect({ case obj: AmfObject => obj })
      .flatMap(o => ctx.factory.builderFor(o).map(_.build()).getOrElse(Nil))
      .toList
}
