package org.mulesoft.als.common.cache

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder, YPartBranch}

import scala.collection.mutable

trait BaseUnitCachedElement[T] {
  protected val unit: BaseUnit

  protected def createElement(location: Location): T

  final def getCachedOrNew(position: Position, uri: String): T =
    getCachedOrNew(Location(position, uri))

  final def getCachedOrNew(position: Location): T = synchronized {
    cache.find(e => e._1 == position) match {
      case Some((_, value)) =>
        value
      case None =>
        val e = createElement(position)
        cache += ((position, e))
        e
    }
  }

  private val cache: mutable.ListBuffer[(Location, T)] = mutable.ListBuffer()
}

case class Location(position: Position, uri: String)

class ObjectInTreeCached(override val unit: BaseUnit, val definedBy: Dialect)
    extends BaseUnitCachedElement[ObjectInTree] {
  override protected def createElement(location: Location): ObjectInTree =
    ObjectInTreeBuilder.fromUnit(
      unit,
      location.uri,
      definedBy,
      NodeBranchBuilder.build(unit, location.position.toAmfPosition, isJson = false)
    )

  def treeWithUpperElement(range: PositionRange, uri: String): Option[ObjectInTree] = {
    val start = getCachedOrNew(range.start, uri)
    val end   = getCachedOrNew(range.end, uri)
    if (start.obj == end.obj) Some(start)
    else if (start.stack.contains(end.obj)) Some(end)
    else if (end.stack.contains(start.obj)) Some(start)
    else None
  }
}

class YPartBranchCached(override val unit: BaseUnit) extends BaseUnitCachedElement[YPartBranch] {
  override protected def createElement(location: Location): YPartBranch = // todo: check if location._2 is not root
    NodeBranchBuilder.build(unit, location.position.toAmfPosition, location.uri.toLowerCase.endsWith(".json"))
}

trait UnitWithCaches {
  protected val unit: BaseUnit
  protected val definedBy: Dialect
  val tree        = new ObjectInTreeCached(unit, definedBy)
  val yPartBranch = new YPartBranchCached(unit)
}
