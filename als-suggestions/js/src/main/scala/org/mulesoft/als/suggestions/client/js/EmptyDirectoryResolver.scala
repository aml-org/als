package org.mulesoft.als.suggestions.client.js

import scala.concurrent.Future
import scala.scalajs.js
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSConverters._

trait EmptyDirectoryResolver {
  val emptyDirectoryResolver: ClientDirectoryResolver = js
    .use(new ClientDirectoryResolver {
      override def exists(path: String): js.Promise[Boolean] =
        Future(false).toJSPromise

      override def readDir(path: String): js.Promise[js.Array[String]] = {
        Future(Seq[String]()).map(_.toJSArray).toJSPromise
      }

      override def isDirectory(path: String): js.Promise[Boolean] = {
        Future(false).toJSPromise
      }
    })
    .as[ClientDirectoryResolver]
}
