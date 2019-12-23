package org.mulesoft.als.client.lsp.edit

sealed trait ClientResourceOperation

import scala.scalajs.js

@js.native
trait ClientNewFileOptions extends js.Object {
  def overwrite: js.UndefOr[Boolean]      = js.native
  def ignoreIfExists: js.UndefOr[Boolean] = js.native
}

@js.native
trait ClientCreateFile extends js.Object {
  def uri: String                               = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

@js.native
trait ClientRenameFile extends js.Object {
  def oldUri: String                            = js.native
  def newUri: String                            = js.native
  def options: js.UndefOr[ClientNewFileOptions] = js.native
}

@js.native
trait ClientDeleteFileOptions extends js.Object {
  def recursive: js.UndefOr[Boolean]         = js.native
  def ignoreIfNotExists: js.UndefOr[Boolean] = js.native
}

@js.native
trait ClientDeleteFile extends js.Object {
  def uri: String                                  = js.native
  def options: js.UndefOr[ClientDeleteFileOptions] = js.native
}
