package org.mulesoft.als.common

import amf.core.remote.File

import scala.concurrent.Future

trait DirectoryResolver {
  val FilePrefix = "file://"

  def dirName(uri: String): String = {
    val lastSeparatorIndex = uri.lastIndexOf("/")

    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0)
      ""
    else
      uri.substring(0, lastSeparatorIndex)
  }

  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]

  def toPath(uri: String) =
    File.unapply(uri).getOrElse(uri)

  def toUri(uri: String) =
    FilePrefix + File.unapply(uri).getOrElse(uri)
}
