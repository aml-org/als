package org.mulesoft.als.server.modules.workspace

import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.feature.link.DocumentLink

trait MainFileTree extends FileTree {

  def references: Map[String, DiagnosticsBundle]

  def contains(uri: String): Boolean

  def profiles: Map[String, ParsedUnit]

  def dialects: Map[String, ParsedUnit]
}

object EmptyFileTree extends MainFileTree {

  override def parsedUnits: Map[String, ParsedUnit] = Map.empty

  override def nodeRelationships: Seq[RelationshipLink] = Nil

  override def documentLinks: Map[String, Seq[DocumentLink]] = Map.empty

  override def aliases: Seq[AliasInfo] = Nil

  override def references: Map[String, DiagnosticsBundle] = Map.empty

  override def contains(uri: String): Boolean = false

  override def profiles: Map[String, ParsedUnit] = Map.empty

  override def dialects: Map[String, ParsedUnit] = Map.empty
}
