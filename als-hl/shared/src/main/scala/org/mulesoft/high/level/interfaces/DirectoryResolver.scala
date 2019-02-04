package org.mulesoft.high.level.interfaces

import scala.concurrent.Future
import scala.language.postfixOps

trait DirectoryResolver {
  val FilePrefix = "file://"

  def dirName(path: String): String = {
    val refinedPath = refinePath(path)

    val lastSeparatorIndex = refinedPath.lastIndexOf("/")

    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0) {
      FilePrefix + ""
    } else {
      FilePrefix + refinedPath.substring(0, lastSeparatorIndex)
    }
  }

  protected def refinePath(path: String): String =
    if (path.startsWith(FilePrefix)) path.substring("file://".length) else path

  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]
}
