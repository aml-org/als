package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{Decorator, KeyProvider, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.language.outline.structure.structureInterfaces.{ContentProvider, StructureNode}

import scala.collection.Seq

class DefaultContentProvider(visibilityFilter: VisibilityFilter,
                             labelProvider: LabelProvider,
                             keyProvider: KeyProvider,
                             decorators: Seq[Decorator])
    extends ContentProvider {

  def buildChildren(node: StructureNode): Seq[DocumentSymbol] = {

    val source: IParseResult = node.getSource
    if (source.isAttr)
      Seq.empty
    else if (source.isElement)
      new StructureBuilder(source, labelProvider, Seq(visibilityFilter, NonEmptyNameVisibilityFilter(labelProvider)))
        .listSymbols(Nil)
        .filter(_.name.length > 0)
    else
      Seq.empty
  }
}
