package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit

import scala.collection.mutable
import scala.concurrent.Future

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String]): CompilableUnit = {
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next)
  }
}

class Repository() {

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = treeKeys.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = units.values.filter(_.inTree)

  def treeKeys: collection.Set[String] = units.filter(_._2.inTree).keySet

  def update(u: BaseUnit): Unit = {
    if (treeKeys.contains(u.id)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(u, inTree = false)
    units.update(u.id, unit)
  }

  def newTree(u: Set[BaseUnit]): Unit = synchronized {
    cleanTree()
    u.map(ParsedUnit(_, inTree = true)).foreach { p =>
      units.update(p.bu.id, p)
    }
  }

  def cleanTree(): Unit = treeKeys.foreach(units.remove)
}
