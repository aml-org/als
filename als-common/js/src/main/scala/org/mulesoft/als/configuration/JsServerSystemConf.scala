package org.mulesoft.als.configuration

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.DirectoryResolver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class JsServerSystemConf(
    clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
    clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver
)

object DefaultJsServerSystemConf extends JsServerSystemConf(js.Array())
