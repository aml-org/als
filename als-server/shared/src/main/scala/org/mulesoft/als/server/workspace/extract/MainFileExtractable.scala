package org.mulesoft.als.server.workspace.extract

trait MainFileExtractable {
  def extractMainFile(cs: String): Option[String]
}
