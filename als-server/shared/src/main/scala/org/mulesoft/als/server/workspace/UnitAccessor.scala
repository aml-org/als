package org.mulesoft.als.server.workspace

import scala.concurrent.Future

/**
  * UnitAccessor provides the functionality to retrieve a current Unit, or the Latest known Unit
  *
  * @tparam UnitType
  */
trait UnitAccessor[UnitType] {
  def isInMainTree(uri: String): Future[Boolean]
  def getUnit(uri: String, uuid: String): Future[UnitType]
  def getLastUnit(uri: String, uuid: String): Future[UnitType]
}