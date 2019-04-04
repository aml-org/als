package org.mulesoft.language.server.internal

import java.net.URI
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait DefaultJVMFileSystem {
  def exists(path: String): Future[Boolean] = Future.successful { Files.exists(toJavaPath(path)) }

  def readDir(path: String): Future[Seq[String]] = Future.successful {
    Files
      .list(toJavaPath(path))
      .map[String](item => item.toFile.getName)
      .collect(Collectors.toList[String])
      .asScala
  }

  def isDirectory(path: String): Future[Boolean] = Future.successful {
    Files.exists(toJavaPath(path))
  }

  def content(fullPath: String): Future[String] = Future.successful {
    new String(Files.readAllBytes(toJavaPath(fullPath)))
  }

  private def toJavaPath(path: String) =
    path match {
      case p if p.startsWith("file://") => Paths.get(p.substring("file://".size))
      case _                            => Paths.get(new URI(path))
    }
}
