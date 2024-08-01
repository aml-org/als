package org.mulesoft.als.common

import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets

import scala.concurrent.Future

trait DirectoryResolver extends AlsPlatformSecrets {
  def exists(path: String): Future[Boolean]

  def readDir(path: String): Future[Seq[String]]

  def isDirectory(path: String): Future[Boolean]

  def toPath(uri: String): String =
    uri.toPath(platform)

}
