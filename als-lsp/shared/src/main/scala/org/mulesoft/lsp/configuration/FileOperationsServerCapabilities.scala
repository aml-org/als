package org.mulesoft.lsp.configuration

case class FileOperationsServerCapabilities(
    didCreate: Option[FileOperationRegistrationOptions],
    willCreate: Option[FileOperationRegistrationOptions],
    didRename: Option[FileOperationRegistrationOptions],
    willRename: Option[FileOperationRegistrationOptions],
    didDelete: Option[FileOperationRegistrationOptions],
    willDelete: Option[FileOperationRegistrationOptions]
)

object FileOperationsServerCapabilities {
  def default: FileOperationsServerCapabilities = FileOperationsServerCapabilities(
    Some(FileOperationRegistrationOptions.default),
    None,
    Some(FileOperationRegistrationOptions.default),
    None,
    Some(FileOperationRegistrationOptions.default),
    None
  )
}
