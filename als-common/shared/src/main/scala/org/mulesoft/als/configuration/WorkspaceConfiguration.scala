package org.mulesoft.als.configuration

trait WorkspaceConfiguration {
  val rootFolder: String
  val mainFile: String
  val cachables: Set[String]
  val profiles: Set[String]
  val semanticExtensions: Set[String]
  val dialects: Set[String]
  def shouldCache(iri: String): Boolean = cachables.contains(iri)
}
