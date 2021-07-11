package org.mulesoft.als.common

import amf.core.remote.Platform

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class PlatformDirectoryResolver(private val platform: Platform) extends DirectoryResolver {
  override def exists(uri: String): Future[Boolean] = platform.fs.asyncFile(FileUtils.getPath(uri, platform)).exists

  override def readDir(uri: String): Future[Seq[String]] =
    platform.fs.asyncFile(FileUtils.getPath(uri, platform)).list.map(array => array.toSeq)

  override def isDirectory(uri: String): Future[Boolean] =
    platform.fs.asyncFile(FileUtils.getPath(uri, platform)).isDirectory

  override def toPath(uri: String): String = FileUtils.getPath(uri, platform)
}
