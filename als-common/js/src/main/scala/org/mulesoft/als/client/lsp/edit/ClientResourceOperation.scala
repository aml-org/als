package org.mulesoft.als.client.lsp.edit

sealed trait ClientResourceOperation

import org.mulesoft.lsp.edit.{CreateFile, DeleteFile, DeleteFileOptions, NewFileOptions, RenameFile}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientNewFileOptions extends js.Object {
  def overwrite: js.UndefOr[Boolean]      = js.native
  def ignoreIfExists: js.UndefOr[Boolean] = js.native
}

object ClientNewFileOptions {
  def apply(internal: NewFileOptions): ClientNewFileOptions =
    js.Dynamic
      .literal(overwrite = internal.overwrite.orUndefined, ignoreIfExists = internal.ignoreIfExists.orUndefined)
      .asInstanceOf[ClientNewFileOptions]
}

@js.native
trait ClientCreateFile extends js.Object {
  def uri: String                               = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

object ClientCreateFile {
  def apply(internal: CreateFile): ClientCreateFile =
    js.Dynamic
      .literal(uri = internal.uri, options = internal.options.map(_.toClient).orUndefined)
      .asInstanceOf[ClientCreateFile]
}

@js.native
trait ClientRenameFile extends js.Object {
  def oldUri: String                            = js.native
  def newUri: String                            = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

object ClientRenameFile {
  def apply(internal: RenameFile): ClientRenameFile =
    js.Dynamic
      .literal(oldUri = internal.oldUri,
               newUri = internal.newUri,
               options = internal.options.map(_.toClient).orUndefined)
      .asInstanceOf[ClientRenameFile]
}

@js.native
trait ClientDeleteFileOptions extends js.Object {
  def recursive: js.UndefOr[Boolean]         = js.native
  def ignoreIfNotExists: js.UndefOr[Boolean] = js.native
}

object ClientDeleteFileOptions {
  def apply(internal: DeleteFileOptions): ClientDeleteFileOptions =
    js.Dynamic
      .literal(recursive = internal.recursive.orUndefined, ignoreIfNotExists = internal.ignoreIfNotExists.orUndefined)
      .asInstanceOf[ClientDeleteFileOptions]
}

@js.native
trait ClientDeleteFile extends js.Object {
  def uri: String                                  = js.native
  def options: js.UndefOr[ClientDeleteFileOptions] = js.native
}

object ClientDeleteFile {
  def apply(internal: DeleteFile): ClientDeleteFile =
    js.Dynamic
      .literal(uri = internal.uri, options = internal.options.map(_.toClient).orUndefined)
      .asInstanceOf[ClientDeleteFile]
}

// $COVERAGE-ON$