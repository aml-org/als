package org.mulesoft.als.common

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.URIImplicits._

import scala.concurrent.Future

trait DirectoryResolver extends PlatformSecrets {
  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]

  def toPath(uri: String): String =
    uri.toPath(platform)

}
