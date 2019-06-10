package org.mulesoft.als.server.lsp4j.internal

import java.net.URI
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

import org.mulesoft.als.common.DirectoryResolver

import scala.collection.JavaConverters._
import scala.concurrent.Future

object DefaultJvmDirectoryResolver extends DirectoryResolver {
  def exists(path: String): Future[Boolean] = Future.successful { Files.exists(toJavaPath(path)) }

  def readDir(path: String): Future[Seq[String]] = Future.successful {
    Files
      .list(toJavaPath(path))
      .map[String](item => item.toFile.getName)
      .collect(Collectors.toList[String])
      .asScala
  }

  def isDirectory(path: String): Future[Boolean] = Future.successful {
    Files.isDirectory(toJavaPath(path))
  }

  private def toJavaPath(path: String) = Paths.get(new URI(path))
}
