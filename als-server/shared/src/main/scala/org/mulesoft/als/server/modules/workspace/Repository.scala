package org.mulesoft.als.server.modules.workspace

import scala.collection.mutable

trait Repository[UnitType] {
  protected val units: mutable.Map[String, UnitType] = mutable.Map.empty
  def getUnit(key: String): Option[UnitType]         = units.get(key)
  def updateUnit(key: String, unit: UnitType): Unit  = units.update(key, unit)
  def removeUnit(key: String): Option[UnitType]      = units.remove(key)
  def getAllFilesUris: List[String]                  = units.keySet.toList
}
