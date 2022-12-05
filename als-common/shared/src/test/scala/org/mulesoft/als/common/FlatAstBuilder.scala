package org.mulesoft.als.common

import org.yaml.model.{YNode, YPart, YScalar}
import org.yaml.parser.{JsonParser, YamlParser}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.mulesoft.als.common.ASTElementWrapper._

class FlatAstBuilder(private val node: YNode) {

  private def flatMapChildren(yPart: YPart): Seq[YPart] = yPart +: yPart.children.flatMap(flatMapChildren)

  private val flatCase = flatMapChildren(node)

  def getNodeForPosition(position: AmfPosition, strict: Boolean = false): Seq[YPart] =
    flatCase.filter(y => {
      y.contains(position, strict)
    })

  def size(): Int = flatCase.size

  def getLastNode(position: AmfPosition, strict: Boolean = false): YPart = getNodeForPosition(position, strict).last

  def assertScalarValue(position: AmfPosition, value: String, strict: Boolean = false): Boolean = {
    val node = getNodeForPosition(position, strict).last
    node.isInstanceOf[YScalar] && node.asInstanceOf[YScalar].value == value
  }

  def assertLastNode(position: AmfPosition, strict: Boolean = false)(condition: YPart => Boolean): Boolean = {
    val node = getNodeForPosition(position, strict).last
    condition(node)
  }
}

object FlatAstBuilder {
  def apply(rawCase: String, isYaml: Boolean = true): FlatAstBuilder =
    if (isYaml)
      new FlatAstBuilder(YamlParser(rawCase).documents().head.node)
    else
      new FlatAstBuilder(JsonParser.withSource(rawCase, "test.json").documents().head.node)
}
