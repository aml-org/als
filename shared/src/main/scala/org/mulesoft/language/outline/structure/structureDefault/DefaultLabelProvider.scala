package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult}
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{YMapEntry, YScalar}
import org.mulesoft.language.outline.common.commonInterfaces.LabelProvider

class DefaultLabelProvider extends LabelProvider {

  def getLabelText(node: IParseResult): String = {
    println("Getting node text")
    if (node.isAttr) {
      var attr = node.asInstanceOf[IAttribute]
      if (attr.value.isDefined) {
        println("Here1")
        return (attr.name + ":") + attr.value.get.toString
      }

    } else if (node.isElement) {

      val hlNode = node.asInstanceOf[IHighLevelNode]

      if (hlNode.definition.key.isDefined &&
        hlNode.definition.key.get.name == RamlDefinitionKeys.DOCUMENTATION_ITEM) {

        val titleAttribute = hlNode.attribute("title")
        if (titleAttribute.isDefined) {
          println("Here2")
          return titleAttribute.get.value.toString
        }

      }
    }

    println("Here3")
    val result = NodeNameProvider.getNodeName(node)
    println("Here4")
    result
  }

  def getTypeText(node: IParseResult): Option[String] = {
    if (!node.isElement)
      return Some("")

    val hlNode = node.asElement.get

    val typeAttribute = hlNode.attribute("type")
    if (typeAttribute.isDefined) {
      var typeValue = typeAttribute.get.value
      if (typeValue.isDefined) {
        Some(":" + typeValue)
      }
    }

    Some("")
  }
}
