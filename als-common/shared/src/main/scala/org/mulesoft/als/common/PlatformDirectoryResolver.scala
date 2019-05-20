package org.mulesoft.als.common

import amf.core.remote.{File, Platform}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class PlatformDirectoryResolver(private val platform: Platform) extends DirectoryResolver {
  override def exists(uri: String): Future[Boolean] = platform.fs.asyncFile(platform.decodeURI(toPath(uri))).exists

  override def readDir(uri: String): Future[Seq[String]] =
    platform.fs.asyncFile(platform.decodeURI(toPath(uri))).list.map(array => array.toSeq)

  override def isDirectory(uri: String): Future[Boolean] =
    platform.fs.asyncFile(platform.decodeURI(toPath(uri))).isDirectory
}
