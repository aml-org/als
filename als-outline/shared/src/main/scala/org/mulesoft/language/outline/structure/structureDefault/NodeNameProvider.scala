package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{YMapEntry, YScalar}

object NodeNameProvider {

  def getNodeName(node: IParseResult): String = {
    if (node.isAttr) {
      if (node.property.isDefined) {
        node.property.get.nameId.get
      } else {
        ""
      }
    } else if (node.isElement) {

      val hlNode = node.asElement.get

      val keyChild = hlNode.children.find(child => {
        var result = child.property.isDefined && isKeyProperty(child.property.get)

        if (result && child.isAttr) {
          val key = child.asAttr.flatMap(_.value).map(_.toString)
          if (hlNode.property.flatMap(_.nameId).contains("items") && node.parent
                .flatMap(_.attribute("name"))
                .flatMap(_.value)
                .map(_.toString) == key) {
            result = false
          }
        }
        result
      })

      if (keyChild.isDefined) {
        if (keyChild.get.isAttr) {
          val attr = keyChild.get.asAttr.get
          if (attr.value.isDefined) {
            attr.value.get.toString
          } else {
            getLowLevelNodeName(node)
          }
        } else {
          getLowLevelNodeName(node)
        }
      } else {
        var result = getLowLevelNodeName(node)
        if (result.isEmpty && node.property.isDefined) {
          var range = node.property.get.range
          if (range.isDefined && !range.get.isArray) {
            result = node.property.get.nameId.map(_.toString).getOrElse("")
          }
        }
        result
      }

    } else if (node.isUnknown) {
      "Unknown"
    } else {
      "Unknown"
    }
  }

  def getLowLevelNodeName(node: IParseResult): String = {
    node.sourceInfo.yamlSources.headOption match {
      case Some(x) =>
        x match {
          case me: YMapEntry =>
            me.key.value match {
              case sc: YScalar => sc.value.toString
              case _           => ""
            }
          case _ => ""
        }
      case _ => ""
    }
  }

  def isKeyProperty(property: IProperty): Boolean = {
    val keyExtra = property
      .getExtra(PropertySyntaxExtra)
      .find(extra => {
        extra.isKey
      })

    keyExtra.isDefined
  }
}
