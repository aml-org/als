package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js

import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

sealed trait ClientResourceOperation extends js.Object {
  def kind: String
}

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
trait ClientCreateFile extends ClientResourceOperation {
  override def kind: String                     = js.native
  def uri: String                               = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

object ClientCreateFile {
  def apply(internal: CreateFile): ClientCreateFile =
    js.Dynamic
      .literal(uri = internal.uri, options = internal.options.map(_.toClient).orUndefined, kind = "create")
      .asInstanceOf[ClientCreateFile]
}

@js.native
trait ClientRenameFile extends ClientResourceOperation {
  override def kind: String                     = js.native
  def oldUri: String                            = js.native
  def newUri: String                            = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

object ClientRenameFile {
  def apply(internal: RenameFile): ClientRenameFile =
    js.Dynamic
      .literal(
        oldUri = internal.oldUri,
        newUri = internal.newUri,
        options = internal.options.map(_.toClient).orUndefined,
        kind = "rename"
      )
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
trait ClientDeleteFile extends ClientResourceOperation {
  override def kind: String                        = js.native
  def uri: String                                  = js.native
  def options: js.UndefOr[ClientDeleteFileOptions] = js.native
}

object ClientDeleteFile {
  def apply(internal: DeleteFile): ClientDeleteFile =
    js.Dynamic
      .literal(uri = internal.uri, options = internal.options.map(_.toClient).orUndefined, kind = "delete")
      .asInstanceOf[ClientDeleteFile]
}

// $COVERAGE-ON$
