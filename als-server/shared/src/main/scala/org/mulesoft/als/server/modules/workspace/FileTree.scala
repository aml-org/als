package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.actions.common.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.feature.link.DocumentLink

trait FileTree {
  def parsedUnits: Map[String, ParsedUnit]
  def nodeRelationships: Seq[RelationshipLink]
  def documentLinks: Map[String, Seq[DocumentLink]]
  def aliases: Seq[AliasInfo]
}
