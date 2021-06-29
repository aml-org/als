package org.mulesoft.als.server.modules.workspace

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.feature.link.DocumentLink

trait MainFileTree extends FileTree {
  def getCache: Map[String, BaseUnit]

  def references: Map[String, DiagnosticsBundle]

  def cleanCache(): Unit

  def contains(uri: String): Boolean

  def cached(uri: String): Option[BaseUnit]
}

object EmptyFileTree extends MainFileTree {
  override def getCache: Map[String, BaseUnit] = Map.empty

  override def parsedUnits: Map[String, ParsedUnit] = Map.empty

  override def nodeRelationships: Seq[RelationshipLink] = Nil

  override def documentLinks: Map[String, Seq[DocumentLink]] = Map.empty

  override def aliases: Seq[AliasInfo] = Nil

  override def references: Map[String, DiagnosticsBundle] = Map.empty

  override def cleanCache(): Unit = {
    // defaults to no action
  }

  override def contains(uri: String): Boolean = false

  override def cached(uri: String): Option[BaseUnit] = None
}
