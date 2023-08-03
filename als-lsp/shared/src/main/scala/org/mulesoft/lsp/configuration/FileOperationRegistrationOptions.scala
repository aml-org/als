package org.mulesoft.lsp.configuration

case class FileOperationRegistrationOptions(filters: Seq[FileOperationFilter])

case class FileOperationFilter(scheme: Option[String], pattern: FileOperationPattern)

case class FileOperationPattern(glob: String, matches: Option[String], options: Option[FileOperationPatternOptions])

case class FileOperationPatternOptions(ignoreCase: Option[Boolean])

object FileOperationRegistrationOptions {
  def default: FileOperationRegistrationOptions =
    FileOperationRegistrationOptions(Seq(FileOperationFilter(Some("file"), FileOperationPattern("**", None, None))))
}
