package org.mulesoft.als.common

import amf.client.parse.IgnoringErrorHandler
import org.yaml.model.{YNode, YPart, YScalar}
import org.yaml.parser.{JsonParser, YamlParser}
import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.als.common.YamlWrapper._

class FlatAstBuilder(private val node: YNode) {

  private def flatMapChildren(yPart: YPart): Seq[YPart] = yPart +: yPart.children.flatMap(flatMapChildren)

  private val flatCase = flatMapChildren(node)

  def getNodeForPosition(position: AmfPosition): Seq[YPart] =
    flatCase.filter(y => {
      y.contains(position)
    })

  def size(): Int = flatCase.size

  def getLastNode(position: AmfPosition): YPart = getNodeForPosition(position).last

  def assertScalarValue(position: AmfPosition, value: String, editionMode: Boolean = true): Boolean = {
    val node = getNodeForPosition(position).last
    node.isInstanceOf[YScalar] && node.asInstanceOf[YScalar].value == value
  }

  def assertLastNode(position: AmfPosition, editionMode: Boolean = true)(condition: YPart => Boolean): Boolean = {
    val node = getNodeForPosition(position).last
    condition(node)
  }
}

object FlatAstBuilder {

  private implicit val eh = IgnoringErrorHandler()

  def apply(rawCase: String, isYaml: Boolean = true) = {
    if (isYaml)
      new FlatAstBuilder(YamlParser(rawCase).documents().head.node)
    else
      new FlatAstBuilder(JsonParser.withSource(rawCase, "test.json").documents().head.node)
  }
}
