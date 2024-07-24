package org.mulesoft.als.configuration

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class JsServerSystemConf(
    clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
    clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver
)

object DefaultJsServerSystemConf extends JsServerSystemConf(js.Array())
