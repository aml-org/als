package org.mulesoft.als.common

import amf.core.internal.remote.File

import scala.concurrent.Future

trait DirectoryResolver {
  val FilePrefix = "file://"
  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]

  def toPath(uri: String): String =
    File.unapply(uri).getOrElse(uri)

}
