package org.mulesoft.als.common

import org.mulesoft.common.client.lexical.{ASTElement, Position => AmfPosition}
import org.yaml.model._

trait ASTPartBranch {
  type T <: ASTElement
  val stack: Seq[T]
  val node: T
  lazy val isMultiline = false
  val isEmptyNode: Boolean
  val stringValue: String
  def keys: Seq[String]
  val isKey: Boolean
  val isValue: Boolean
  val isAtRoot: Boolean
  val isArray: Boolean
  lazy val isInArray: Boolean = false
  val strict: Boolean
  val parent: Option[T] = stack.headOption
  val position: AmfPosition
  def parentKey: Option[String]
  def brothers: Seq[T]

  def isKeyDescendantOf(key: String): Boolean =
    isKey && parentEntryIs(key)

  def parentEntryIs(key: String): Boolean = parentKey.contains(key)

  def getAncestor(l: Int): Option[T] = stack.lift(l)

  def isInBranchOf(key: String): Boolean =
    stack.exists({
      case e: YMapEntry => e.key.asScalar.exists(_.text == key)
      case _            => false
    })

  def isValueDescendanceOf(key: String): Boolean = isValue && parentEntryIs(key)

  def brothersKeys: Set[String] =
    brothers
      .flatMap({
        case yme: YMapEntry => yme.key.asScalar.map(_.text)
        case _              => None
      })
      .toSet

  lazy val isKeyLike: Boolean = isKey || isInArray

  def closestEntry: Option[T]

  @scala.annotation.tailrec
  protected final def findFirstOf[Element <: T](clazz: Class[Element], l: Seq[T]): Option[Element] = {
    l match {
      case Nil                                 => None
      case head :: _ if clazz.isInstance(head) => Some(head.asInstanceOf[Element])
      case _ :: Nil                            => None
      case _ :: tail                           => findFirstOf(clazz, tail)
    }
  }
}
