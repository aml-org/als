package org.mulesoft.als.common

import amf.core.internal.remote.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlatformDirectoryResolver(override val platform: Platform) extends DirectoryResolver {
  override def exists(uri: String): Future[Boolean] = platform.fs.asyncFile(toPath(uri)).exists

  override def readDir(uri: String): Future[Seq[String]] =
    platform.fs.asyncFile(toPath(uri)).list.map(array => array.toSeq)

  override def isDirectory(uri: String): Future[Boolean] =
    platform.fs.asyncFile(FileUtils.getPath(uri, platform)).isDirectory
}
