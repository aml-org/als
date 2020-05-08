package org.mulesoft.als.server.workspace

import org.mulesoft.als.actions.common.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.Future

trait UnitWorkspaceManager {
  def getRootOf(uri: String): Option[String]
  def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]]
  def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]]
  def getRelationships(uri: String, uuid: String): Future[Seq[RelationshipLink]]
}