package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit

trait MainFileTree {
  def getCache: Map[String, BaseUnit]

  def parsedUnits: Map[String, ParsedUnit]

  def references: Map[String, DiagnosticsBundle]

  def cleanCache(): Unit

  def contains(uri: String): Boolean

  def cached(uri: String): Option[BaseUnit]
}

object EmptyFileTree extends MainFileTree {
  override def getCache: Map[String, BaseUnit] = Map.empty

  override def parsedUnits: Map[String, ParsedUnit] = Map.empty

  override def references: Map[String, DiagnosticsBundle] = Map.empty

  override def cleanCache(): Unit = {}

  override def contains(uri: String): Boolean = false

  override def cached(uri: String): Option[BaseUnit] = None
}
