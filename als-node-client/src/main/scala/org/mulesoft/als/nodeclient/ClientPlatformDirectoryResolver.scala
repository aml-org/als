package org.mulesoft.als.nodeclient

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.configuration.ClientDirectoryResolver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise

class ClientPlatformDirectoryResolver(platform: Platform) extends ClientDirectoryResolver {
  private val internalResolver = new PlatformDirectoryResolver(platform)

  override def exists(path: String): Promise[Boolean] = internalResolver.exists(path).toJSPromise

  override def readDir(path: String): Promise[js.Array[String]] =
    internalResolver.readDir(path).map(_.toJSArray).toJSPromise

  override def isDirectory(path: String): Promise[Boolean] = internalResolver.isDirectory(path).toJSPromise
}
