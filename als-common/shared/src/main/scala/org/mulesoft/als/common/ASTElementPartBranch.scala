package org.mulesoft.als.common

import org.mulesoft.antlrast.ast.{ASTNode, Node, Terminal}
import org.mulesoft.common.client.lexical.ASTElement
import org.mulesoft.common.client.lexical.{Position => AmfPosition}

case class ASTElementPartBranch(override val node: ASTNode, override val position: AmfPosition, stack: Seq[ASTNode])
    extends ASTPartBranch {
  override type T = ASTNode
  override val isEmptyNode: Boolean = node match {
    case t: Terminal => t.value.isEmpty
    case _           => false
  }
  override val stringValue: String = node.toString()

  override def keys: Seq[String] = node match {
    case n: Node => n.children.collect({ case childrenNode: Node => childrenNode.name })
    case _       => Nil
  }

  override val isKey: Boolean = node
    .isInstanceOf[ASTElement] && parent.collectFirst({ case n: Node if n.children.head == node => n }).isDefined
  override val isValue: Boolean  = !isKey
  override val isAtRoot: Boolean = stack.isEmpty
  override val isArray: Boolean  = false
  override val strict: Boolean   = false

  override def parentKey: Option[String] = None // TODO: analize parent Node and look for first terminal with name?

  override def brothers: Seq[ASTNode] = Nil // TODO

  override def closestEntry: Option[Node] = findFirstOf(classOf[Node], stack) // TODO:?
}
