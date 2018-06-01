package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{Decorator, KeyProvider, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureInterfaces.{ContentProvider, StructureConfiguration, StructureNode}

import scala.collection.Seq
import scala.collection.mutable.ArrayBuffer


class DefaultContentProvider(visibilityFilter: VisibilityFilter,
                             labelProvider: LabelProvider,
                             keyProvider: KeyProvider,
                             decorators: Seq[Decorator]) extends ContentProvider {

  def buildChildren(node: StructureNode): Seq[StructureNode] = {

      println("Getting children of Structured node " + node.text)

      val source: IParseResult = node.getSource
      println("Got source")

      if (source.isAttr) {

        println("Source is attr")
        Seq.empty

      } else if (source.isElement) {

        println("Getting children of HL node")

        val sourceChildren = source.children
        println("Original children number: " + sourceChildren.length)

        val filteredSourceChildren = sourceChildren.filter(child => {
          !child.isAttr //&& !child.isUnknown
        })

        println("Filtered children number: " + filteredSourceChildren.length)

        val result = filteredSourceChildren.filter(child=>visibilityFilter.apply(child)).map(child=>{
          StructureBuilder.hlNodeToStructureNode(child, None, labelProvider, keyProvider, decorators)
        })

        println("Found children number: " + result.length)
        result
      } else {

        println("Source is unknown")
        Seq.empty
      }
    }
}
