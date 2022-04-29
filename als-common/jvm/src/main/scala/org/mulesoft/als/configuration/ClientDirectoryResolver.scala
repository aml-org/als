package org.mulesoft.als.configuration

import java.util.concurrent.CompletableFuture
import org.mulesoft.als.common.{DirectoryResolver => InternalResolver}
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
trait ClientDirectoryResolver {
  def exists(path: String): CompletableFuture[Boolean]

  def readDir(path: String): CompletableFuture[java.util.List[String]]

  def isDirectory(path: String): CompletableFuture[Boolean]
}

object DirectoryResolverAdapter {
  def convert(clientResolver: ClientDirectoryResolver): InternalResolver = {
    new InternalResolver {

      override def exists(path: String): Future[Boolean] =
        clientResolver.exists(toPath(path)).toScala

      override def readDir(path: String): Future[Seq[String]] =
        clientResolver.readDir(toPath(path)).toScala.map(_.asScala.toSeq)

      override def isDirectory(path: String): Future[Boolean] =
        clientResolver.isDirectory(toPath(path)).toScala

    }
  }
}
