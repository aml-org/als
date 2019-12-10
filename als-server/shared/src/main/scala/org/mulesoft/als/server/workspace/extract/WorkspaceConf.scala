package org.mulesoft.als.server.workspace.extract

case class WorkspaceConf(rootFolder: String,
                         mainFile: String,
                         cachables: Set[String],
                         configReader: Option[ConfigReader]) {

  def shouldCache(iri: String): Boolean = cachables.contains(iri)
}
