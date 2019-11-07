package org.mulesoft.als.server.workspace

trait MainFileExtractable {
  def extractMainFile(cs: CharSequence): Option[String]
}
