package org.mulesoft.als.suggestions.js

import scala.scalajs.js
import scala.scalajs.js.Promise

/**
  * File system provider for
  */
@js.native
trait IFSProvider extends js.Object {

  def contentAsync(fullPath: String): Promise[String] = js.native

  def dirName(fullPath: String): String = js.native

  def name(fullPath: String): String = js.native

  def existsAsync(path: String): Promise[Boolean] = js.native

  def resolve(contextPath: String, relativePath: String): Option[String] = js.native

  def isDirectory(fullPath: String): Boolean = js.native

  def readDirAsync(path: String): Promise[js.Array[String]] = js.native

  def isDirectoryAsync(path: String): Promise[Boolean] = js.native

  //def contentDirName(content: IEditorStateProvider): String

  def content(fullPath: String): String

  def exists(fullPath: String): Boolean

  def readDir(fullPath: String): js.Array[String]

  def separatorChar(): Char
}
