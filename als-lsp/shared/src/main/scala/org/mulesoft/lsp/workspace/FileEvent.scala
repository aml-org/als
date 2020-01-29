package org.mulesoft.lsp.workspace

import FileChangeType.FileChangeType

case class FileEvent(uri: String, `type`: FileChangeType)

case object FileChangeType extends Enumeration {
  type FileChangeType = Value

  val Created: Value = Value(1)
  val Changed: Value = Value(2)
  val Deleted: Value = Value(3)
}
