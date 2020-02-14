package org.mulesoft.als.configuration

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global

object EmptyJsDirectoryResolver extends ClientDirectoryResolver {

  override def exists(path: String): js.Promise[Boolean] =
    Future(false).toJSPromise

  override def readDir(path: String): js.Promise[js.Array[String]] = {
    Future(Seq[String]()).map(_.toJSArray).toJSPromise
  }

  override def isDirectory(path: String): js.Promise[Boolean] = {
    Future(false).toJSPromise
  }
}
