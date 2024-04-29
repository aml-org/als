package org.mulesoft.lsp.edit

sealed trait ResourceOperation

/** Options to create a file.
  *
  * @param overwrite
  *   Overwrite existing file. Overwrite wins over `ignoreIfExists`
  * @param ignoreIfExists
  *   Ignore if exists.
  */
case class NewFileOptions(overwrite: Option[Boolean], ignoreIfExists: Option[Boolean])

/** Create file operation
  *
  * @param uri
  *   The resource to create.
  * @param options
  *   Additional options
  */
case class CreateFile(uri: String, options: Option[NewFileOptions]) extends ResourceOperation

/** Rename file operation
  *
  * @param oldUri
  *   The old (existing) location.
  * @param newUri
  *   The new location.
  * @param options
  *   Additional options.
  */
case class RenameFile(oldUri: String, newUri: String, options: Option[NewFileOptions]) extends ResourceOperation

/** Delete file options
  *
  * @param ignoreIfNotExists
  *   Ignore the operation if the file doesn't exist.
  * @param recursive
  *   Delete the content recursively if a folder is denoted.
  */
case class DeleteFileOptions(recursive: Option[Boolean], ignoreIfNotExists: Option[Boolean])

/** Delete file operation
  *
  * @param uri
  *   The file to delete.
  * @param options
  *   Delete options.
  */
case class DeleteFile(uri: String, options: Option[DeleteFileOptions]) extends ResourceOperation
