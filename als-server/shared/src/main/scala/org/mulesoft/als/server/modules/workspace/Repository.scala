package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

case class ParsedUnit(bu: BaseUnit, inTree: Boolean)

class Repository() {

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private val processing: mutable.Map[String, Promise[ParsedUnit]] = mutable.Map.empty

  def getUnit(uri: String): Future[ParsedUnit] =
    units
      .get(uri)
      .map(Future.successful)
      .getOrElse({
        processing
          .getOrElse(uri, {
            val promisedUnit = Promise[ParsedUnit]()
            processing.put(uri, promisedUnit)
            promisedUnit
          })
          .future
      })

  def inTree(uri: String): Boolean = units.get(uri).exists(_.inTree)

  def update(uri: String, u: BaseUnit, inTree: Boolean): Unit = {
    val unit = ParsedUnit(u, inTree)
    units.update(uri, unit)
    updateProssessing(unit)
  }

  private def updateProssessing(u: ParsedUnit): Unit = {
    processing.get(u.bu.id).foreach { p =>
      p.success(u)
    }
    processing.remove(u.bu.id)
  }

}
