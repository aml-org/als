package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.Decorator
import org.mulesoft.language.outline.structure.structureDefaultInterfaces.Decoration

import scala.collection.mutable

class DefaultOASDecorator extends Decorator {

  val decorations: mutable.HashMap[String, Decoration] = new mutable.HashMap[String, Decoration]

  def addDecoration(nodeType: String, decoration: Decoration): Unit = {

    this.decorations(nodeType) = decoration
  }

  def getNodeType(node: IParseResult): String = {

    //    try {
    //      println(s"Getting type for node name of ${NodeNameProvider.getNodeName(node)}")
    //    } catch {
    //      case th: Throwable => th.printStackTrace()
    //    }

    if (node.isAttr) {
      OASNodeTypes.ATTRIBUTE
    } else if (node.isElement) {

      val hlNode = node.asElement.get

      if (hlNode.definition.nameId.isDefined) {

        val nodeDefinition = hlNode.definition.nameId.get

        //        try {
        //          println(s"Definition name is ${nodeDefinition}")
        //        } catch {
        //          case th: Throwable => th.printStackTrace()
        //        }

        if (nodeDefinition == OASDefinitionKeys.PathsObject) {
          OASNodeTypes.PATHS_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.PathItemObject) {
          OASNodeTypes.PATH_ITEM
        } else if (nodeDefinition == OASDefinitionKeys.OperationObject) {
          OASNodeTypes.OPERATION_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.DefinitionObject) {
          OASNodeTypes.DEFINITION_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.SchemaObject) {

          OASNodeTypes.SCHEMA_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.ItemsObject) {
          OASNodeTypes.ITEMS_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.ParameterObject ||
                   nodeDefinition == OASDefinitionKeys.ParameterDefinitionObject) {
          OASNodeTypes.PARAMETER_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.Response ||
                   nodeDefinition == OASDefinitionKeys.ResponseDefinitionObject) {

          OASNodeTypes.RESPONSE_OBJECT
        } else if (nodeDefinition == OASDefinitionKeys.ItemsObject) {
          OASNodeTypes.ITEMS_OBJECT
        } else {
          OASNodeTypes.OTHER
        }
      } else {
        OASNodeTypes.OTHER
      }

    } else {
      OASNodeTypes.OTHER
    }
  }

  def getDecoration(node: IParseResult): Option[Decoration] = {
    val nodeType = this.getNodeType(node)

    this.decorations.get(nodeType)
  }

  def getIcon(node: IParseResult): Option[String] = {

    //    try {
    //      println(s"Getting icon for node name of ${NodeNameProvider.getNodeName(node)} of type ${getNodeType(node)}")
    //    } catch {
    //      case th: Throwable => th.printStackTrace()
    //    }

    val decoration = this.getDecoration(node)
    if (decoration.isDefined) {
      Some(decoration.get.icon)
    } else {
      None
    }
  }

  def getTextStyle(node: IParseResult): Option[String] = {

    val decoration = this.getDecoration(node)
    if (decoration.isDefined) {
      Some(decoration.get.textStyle)
    } else {
      None
    }
  }
}
