package org.mulesoft.language.test.serverConnection

import amf.core.unsafe.PlatformSecrets

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

object FS extends PlatformSecrets {
  def exists(path: String): Future[Boolean] = platform.fs.asyncFile(path).exists

  def readDir(path: String): Future[Array[String]] = platform.fs.asyncFile(path).list

  def isDirectory(path: String): Future[Boolean] = platform.fs.asyncFile(path).isDirectory

  def content(path: String): Future[String] = {
    throw new Error("not implemented")
  }

  private def removeProtocol(path: String): String = {
    if (path.indexOf("file:///") == 0) path.replace("file:///", "/")
    else path
  }
}
