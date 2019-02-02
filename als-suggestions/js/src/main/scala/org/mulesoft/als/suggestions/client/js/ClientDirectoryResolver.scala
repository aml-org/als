package org.mulesoft.als.suggestions.client.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

@js.native
trait ClientDirectoryResolver extends js.Object {

  def exists(path: String): js.Promise[Boolean] = js.native

  def readDir(path: String): js.Promise[js.Array[String]] = js.native

  def isDirectory(path: String): js.Promise[Boolean] = js.native

}
