package org.mulesoft.language.outline.structure.structureDefault


import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureDefaultInterfaces.Decoration

import scala.collection.mutable;
import org.mulesoft.language.outline.common.commonInterfaces.Decorator

class DefaultRAMLDecorator extends Decorator {

  val decorations: mutable.HashMap[String, Decoration] = new mutable.HashMap[String, Decoration]

  def addDecoration(nodeType: String, decoration: Decoration): Unit = {

    this.decorations(nodeType) = decoration
  }

  def getNodeType(node: IParseResult): String = {
    if (node.isAttr) {

      RamlNodeTypes.ATTRIBUTE
    } else if (node.isElement){

      val hlNode = node.asElement.get
      if (hlNode.definition.nameId.isDefined){

        val nodeDefinition = hlNode.definition.nameId.get

        if (nodeDefinition == RamlDefinitionKeys.RESOURCE) {
          RamlNodeTypes.RESOURCE
        }
        else if (nodeDefinition == RamlDefinitionKeys.METHOD) {
          RamlNodeTypes.METHOD
        }
        else if (nodeDefinition == RamlDefinitionKeys.ABSTRACT_SECURITY_SCHEME) {
          RamlNodeTypes.SECURITY_SCHEME
        }
//        else if (nodeDefinition == RamlDefinitionKeys.TYPE_DECLARATION) {
//
//          RamlNodeTypes.ANNOTATION_DECLARATION
//        }
        else if (nodeDefinition == RamlDefinitionKeys.TYPE_DECLARATION) {

          RamlNodeTypes.TYPE_DECLARATION
        }
        else if (nodeDefinition == RamlDefinitionKeys.DOCUMENTATION_ITEM) {
          RamlNodeTypes.DOCUMENTATION_ITEM
        }
//        if ((node.lowLevel().unit() != node.root().lowLevel().unit())) {
//          return defaultInterfaces.NodeType.EXTERNAL_UNIT
//
//        }

        RamlNodeTypes.OTHER
      } else {
        RamlNodeTypes.OTHER
      }

    }

    RamlNodeTypes.OTHER
  }

  def getDecoration(node: IParseResult): Option[Decoration] = {
    val nodeType = this.getNodeType(node)

    this.decorations.get(nodeType)
  }

  def getIcon(node: IParseResult): Option[String] = {

    val decoration = this.getDecoration(node)
    if (decoration.isDefined){
      Some(decoration.get.icon)
    } else {
      Some(Icons.TAG)
    }
  }

  def getTextStyle(node: IParseResult): Option[String] = {

    val decoration = this.getDecoration(node)
    if (decoration.isDefined){
      Some(decoration.get.textStyle)
    } else {
      Some(TextStyles.NORMAL)
    }
  }
}
