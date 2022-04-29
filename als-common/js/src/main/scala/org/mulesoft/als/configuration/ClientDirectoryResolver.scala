package org.mulesoft.als.configuration

import org.mulesoft.als.common.{DirectoryResolver => InternalResolver}

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.concurrent.ExecutionContext.Implicits.global

@ScalaJSDefined
trait ClientDirectoryResolver extends js.Object {

  def exists(path: String): js.Promise[Boolean]

  def readDir(path: String): js.Promise[js.Array[String]]

  def isDirectory(path: String): js.Promise[Boolean]
}

object DirectoryResolverAdapter {
  def convert(clientResolver: ClientDirectoryResolver): InternalResolver = {
    new InternalResolver {

      override def exists(path: String): Future[Boolean] =
        clientResolver.exists(toPath(path)).toFuture

      override def readDir(path: String): Future[Seq[String]] =
        clientResolver.readDir(toPath(path)).toFuture.map(_.toSeq)

      override def isDirectory(path: String): Future[Boolean] =
        clientResolver.isDirectory(toPath(path)).toFuture

    }
  }
}
