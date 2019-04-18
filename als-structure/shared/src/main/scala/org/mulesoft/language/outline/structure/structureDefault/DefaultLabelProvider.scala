package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult}
import org.mulesoft.language.outline.common.commonInterfaces.LabelProvider
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._

class DefaultLabelProvider extends LabelProvider {

  def getLabelText(node: IParseResult): String = {
    var resultOpt: Option[String] = None
    if (node.isAttr) {
      var attr = node.asInstanceOf[IAttribute]
      if (attr.value.isDefined) {
        val valString = attr.name
        resultOpt = Some(valString)
      }

    } else if (node.isElement) {

      val hlNode = node.asInstanceOf[IHighLevelNode]

      if (hlNode.definition.nameId.contains(RamlDefinitionKeys.DOCUMENTATION_ITEM)) {

        val titleAttribute = hlNode.attribute("title")
        if (titleAttribute.isDefined) {
          val valString = titleAttribute.get.value.map(_.toString).getOrElse("")
          resultOpt = Some(valString)
        }

      } else if (hlNode.definition.nameId.contains(RamlDefinitionKeys.USES_DECLARATION)) {
        val titleAttribute = hlNode.attribute("key")
        if (titleAttribute.isDefined) {
          val valString = titleAttribute.get.value.map(_.toString).getOrElse("")
          resultOpt = Some(valString)
        }
      } else if (hlNode.definition.isAssignableFrom(OASDefinitionKeys.ParameterObject) || hlNode.definition.nameId
        .contains(OASDefinitionKeys.TagObject)) {
        val titleAttribute = hlNode.attribute("name")
        if (titleAttribute.isDefined) {
          val valString = titleAttribute.get.value.map(_.toString).getOrElse("")
          resultOpt = Some(valString)
        }
      }
    }
    if (resultOpt.isEmpty) {
      val valString = NodeNameProvider.getNodeName(node)
      resultOpt = Some(valString)
    }
    resultOpt.get
  }

  def getTypeText(node: IParseResult): Option[String] = {
    if (!node.isElement)
      return Some("")

    val hlNode = node.asElement.get

    var typeAttribute: Option[IAttribute] = None
    if (hlNode.definition.isAssignableFrom("ResourceBase")) {
      typeAttribute = hlNode.element("type").flatMap(_.attribute("name"))
    } else {
      typeAttribute = hlNode.attribute("type")
    }
    typeAttribute
      .flatMap(_.value)
      .map(x =>
        x match {
          case jw: JSONWrapper =>
            var strVal: String = jw.kind match {
              case STRING => s":${jw.value(STRING).get}"
              case NUMBER => s":${jw.value(NUMBER).get}"
              case BOOLEAN => s":${jw.value(BOOLEAN).get}"
              case _ => ""
            }
            strVal
          case _ => s":$x"
        })
      .orElse(Some(""))
  }
}
