package org.mulesoft.als.server.workspace.extract

case class WorkspaceConf(configFileUri: String, mainFile: String, cachables: Set[String], configReader: ConfigReader) {

  def shouldCache(iri: String): Boolean = cachables.contains(iri)
}
