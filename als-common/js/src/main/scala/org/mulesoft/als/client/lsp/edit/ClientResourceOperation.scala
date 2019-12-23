package org.mulesoft.als.client.lsp.edit

sealed trait ResourceOperation

import org.mulesoft.lsp.edit.{CreateFile, DeleteFileOptions, NewFileOptions, RenameFile}
import org.mulesoft.als.client.convert.LspConverters._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "NewFileOptions")
class ClientNewFileOptions(private val internal: NewFileOptions) {
  def overwrite: js.UndefOr[Boolean]      = internal.overwrite.orUndefined
  def ignoreIfExists: js.UndefOr[Boolean] = internal.ignoreIfExists.orUndefined
}

@JSExportAll
@JSExportTopLevel(name = "CreateFile")
class ClientCreateFile(private val internal: CreateFile) {
  def uri: String                               = internal.uri
  def options: js.UndefOr[ClientNewFileOptions] = internal.options.map(toClientNewFileOptions).orUndefined
}

@JSExportAll
@JSExportTopLevel(name = "RenameFile")
class ClientRenameFile(private val internal: RenameFile) {
  def oldUri: String                            = internal.oldUri
  def newUri: String                            = internal.newUri
  def options: js.UndefOr[ClientNewFileOptions] = internal.options.map(toClientNewFileOptions).orUndefined
}

@JSExportAll
@JSExportTopLevel(name = "DeleteFileOptions")
case class ClientDeleteFileOptions(recursive: Option[Boolean], ignoreIfNotExists: Option[Boolean])

@JSExportAll
@JSExportTopLevel(name = "DeleteFile")
case class ClientDeleteFile(uri: String, options: Option[DeleteFileOptions]) extends ResourceOperation
