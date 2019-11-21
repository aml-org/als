package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String]): CompilableUnit = {
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next)
  }
}

class Repository() {

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = units.get(uri).exists(_.inTree)

  def treeUnits(): Iterable[ParsedUnit] = units.values.filter(_.inTree)

  def update(uri: String, u: BaseUnit, inTree: Boolean): Unit = {
    val unit = ParsedUnit(u, inTree)
    units.update(uri, unit)
  }
}
