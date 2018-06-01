package org.mulesoft.language.outline.structure.structureImpl

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces._
import org.mulesoft.language.outline.structure.structureInterfaces.{ContentProvider, StructureConfiguration, StructureNode}

import scala.collection._
import scala.collection.mutable.ArrayBuffer

class StructureBuilder (private val config: StructureConfiguration) {

  def getStructureForAllCategories: Map[String, StructureNode] = {
    println("Starting to build structure")
    val hlRootOption = this.config.astProvider.getASTRoot

    if (hlRootOption.isEmpty){

      immutable.HashMap()
    } else {


      val selectedOption = this.config.astProvider.getSelectedNode
      val structureRoot = StructureBuilder.hlNodeToStructureNode(
        hlRootOption.get, selectedOption,
        this.config.labelProvider,
        this.config.keyProvider,
        this.config.decorators)

      println("Starting to build tree")
      this.buildTreeRecursively(structureRoot, this.config.contentProvider)
      println("Finished to build tree")

      val result: mutable.HashMap[String, StructureNode] = mutable.HashMap()

      this.config.categories.keys.foreach{
        case (categoryName)=>{
          val categoryFilter = this.config.categories(categoryName)
          val filteredTree = this.filterTreeByCategory(structureRoot, categoryName, categoryFilter)

          result(categoryName) = filteredTree
        }
      }

      println("Finished to build structure")
      result
    }
  }

  def buildTreeRecursively(structureNode: StructureNodeImpl, contentProvider: ContentProvider): Unit = {

    val children = contentProvider.buildChildren(structureNode)

    if (children.nonEmpty) {

      structureNode.children = children

      children.foreach(child => {

        if(child.isInstanceOf[StructureNodeImpl])
          this.buildTreeRecursively(child.asInstanceOf[StructureNodeImpl], contentProvider)
      })

    }
    else {
      structureNode.children = ArrayBuffer()
    }

  }

  def filterTreeByCategory(root: StructureNode, categoryName: String, categoryFilter: CategoryFilter): StructureNode = {

    if (root.children.isEmpty) {

      root
    } else {

      val result = cloneNode(root)

      var filteredChildren = root.children

      filteredChildren = root.children.filter(child=>categoryFilter.apply(child.getSource))//_underscore_.filter(root.children, (child => filter(child.getSource()))))
      filteredChildren.foreach(child => child.asInstanceOf[StructureNodeImpl].category = categoryName)


      result.asInstanceOf[StructureNodeImpl].children = filteredChildren

      result
    }
  }

  def cloneNode(toClone: StructureNode): StructureNode = {

    val result: StructureNodeImpl = new StructureNodeImpl(toClone.getSource)

    result.text = toClone.text
    result.typeText = toClone.typeText
    result.icon = toClone.icon
    result.textStyle = toClone.textStyle
    result.children = toClone.children
    result.key = toClone.key
    result.start = toClone.start
    result.end = toClone.end
    result.selected = toClone.selected
    result.category = toClone.category

    result
  }


}

object StructureBuilder {

  def hlNodeToStructureNode(hlNode: IParseResult,
                            selected: Option[IParseResult],
                            labelProvider: LabelProvider,
                            keyProvider: KeyProvider,
                            decorators: Seq[Decorator]): StructureNodeImpl = {

    println("Converting node ")
    val result = new StructureNodeImpl(hlNode)

    result.text = labelProvider.getLabelText(hlNode)
    println("Node text is: " + result.text)

//    println("Converting node " +
//      (if(hlNode.isElement) hlNode.asElement.get.definition.nameId.get else if (hlNode.isAttr)hlNode.asAttr.get.name else "Unknown"))


    result.typeText = labelProvider.getTypeText(hlNode)

    val decoratorOption = getDecorator(result, decorators)
    if (decoratorOption.isDefined) {
      val iconOption = decoratorOption.get.getIcon(hlNode)
      result.icon = if(iconOption.isDefined) iconOption.get else ""

      val textStyleOption = decoratorOption.get.getTextStyle(hlNode)
      result.textStyle = if(textStyleOption.isDefined) textStyleOption.get else ""
    }

    result.key = keyProvider.getKey(hlNode)

    val nodeRange = this.nodeRange(hlNode)

    result.start = nodeRange.start
    result.end = nodeRange.end

    if (selected.isDefined && selected.get == hlNode) {
      result.selected = true
    }

    println("Finished converting node")

    result

  }

  def getDecorator(node: StructureNode, decorators: Seq[Decorator]): Option[Decorator] = {

    val source = node.getSource

    decorators.find(decorator=>{
      decorator.getIcon(source).isDefined || decorator.getTextStyle(source).isDefined
    })

  }

  def nodeRange(node: IParseResult): IRange = {
    val ranges = node.sourceInfo.ranges
    if (ranges.isEmpty) {

      IRange (0, 0)
    } else {
      var min = ranges(0).start.position
      var max = ranges(0).end.position

      ranges.foreach(range=>{

        if (range.start.position < min) {
          min = range.start.position
        }

        if (range.end.position > max) {
          max = range.end.position
        }
      })

      IRange(min, max)
    }
  }
}
