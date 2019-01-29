package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{Decorator, KeyProvider, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureInterfaces.{ContentProvider, StructureNode}

import scala.collection.Seq


class DefaultContentProvider(visibilityFilter: VisibilityFilter,
                             labelProvider: LabelProvider,
                             keyProvider: KeyProvider,
                             decorators: Seq[Decorator]) extends ContentProvider {

  def buildChildren(node: StructureNode): Seq[StructureNode] = {


    val source: IParseResult = node.getSource

    if (source.isAttr) {

      Seq.empty

    } else if (source.isElement) {


      val sourceChildren = source.children

      val filteredSourceChildren = sourceChildren.filter(child => {
        !child.isAttr //&& !child.isUnknown
      })


      val result = filteredSourceChildren.filter(child => visibilityFilter.apply(child)).map(child => {
        StructureBuilder.hlNodeToStructureNode(child, None, labelProvider, keyProvider, decorators)
      }).filter(child => child.text.length > 0)

      result
    } else {

      Seq.empty
    }
  }
}
