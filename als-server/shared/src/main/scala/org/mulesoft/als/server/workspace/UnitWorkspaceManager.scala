package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.Future

trait UnitWorkspaceManager {
  def getProjectRootOf(uri: String): Future[Option[String]]
  def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]]

  /** gets all Project document links */
  def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]]
  def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]]
  def getRelationships(uri: String, uuid: String): Future[(CompilableUnit, Seq[RelationshipLink])]
}
