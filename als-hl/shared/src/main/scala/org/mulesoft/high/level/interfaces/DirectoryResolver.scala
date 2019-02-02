package org.mulesoft.high.level.interfaces

import scala.concurrent.Future
import scala.language.postfixOps

trait DirectoryResolver {
  def dirName(path: String): String = {
    val lastSeparatorIndex = path.lastIndexOf("/")
    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0) {
      ""
    } else {
      path.substring(0, lastSeparatorIndex)
    }
  }

  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]
}
