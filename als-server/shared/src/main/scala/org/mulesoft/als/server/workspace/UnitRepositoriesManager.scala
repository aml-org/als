package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.workspace.CompilableUnit

import scala.concurrent.Future

trait UnitRepositoriesManager {
  def getCU(uri: String, uuid: String): Future[CompilableUnit]
  def getLastCU(uri: String, uuid: String): Future[CompilableUnit]
  def getRootOf(uri: String): Option[String]
}
