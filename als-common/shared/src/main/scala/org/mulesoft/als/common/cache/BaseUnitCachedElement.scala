package org.mulesoft.als.common.cache

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.Position
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
    ObjectInTreeBuilder.fromUnit(unit, location.position.toAmfPosition, Some(location.uri), definedBy)
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
