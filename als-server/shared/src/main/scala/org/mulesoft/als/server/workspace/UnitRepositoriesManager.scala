package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.Future

trait UnitRepositoriesManager {
  def getCU(uri: String, uuid: String): Future[CompilableUnit]
  def getLastCU(uri: String, uuid: String): Future[CompilableUnit]
  def getRootOf(uri: String): Option[String]
  def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]]
  def getAliases(uri: String, uuid: String): Future[Seq[(SourceLocation, SourceLocation)]]
  def getRelationships(uri: String, uuid: String): Future[Seq[(Location, Location)]]
}