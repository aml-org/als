package org.mulesoft.als

import org.mulesoft.als.configuration.WorkspaceConfiguration
import org.mulesoft.amfintegration.InitOptions

import scala.concurrent.Future

trait CompilerResult[CU, EH, MetaData] {
  val baseUnit: CU
  val eh: EH
  val definedBy: MetaData
  def tree: Set[String]
}

trait CompilerEnvironment[CU, EH, MetaData, ENV] {

  def modelBuilder(): ModelBuilder[CU, EH, ENV, MetaData]
  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit]
}

trait ModelBuilder[CU, EH, ENV, MetaData] {
  type CR <: CompilerResult[CU, EH, MetaData]
  def parse(uri: String): Future[CR]
  def parse(uri: String, env: ENV, workspaceConfiguration: Option[WorkspaceConfiguration]): Future[CR]
  def indexMetadata(url: String, content: Option[String]): Future[MetaData]
  def fullResolution(unit: CU, eh: EH): CU
}
