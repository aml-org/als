package org.mulesoft.als.configuration

import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class JsServerSystemConf(clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                              clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver)
    extends PlatformSecrets {

  def environment: Environment =
    Environment()
      .withLoaders(
        if (clientLoaders.isEmpty) {
          platform.loaders()
        } else {
          clientLoaders.map(ResourceLoaderConverter.internalResourceLoader).toSeq
        }
      )

  def directoryResolver: DirectoryResolver =
    DirectoryResolverAdapter.convert(clientDirResolver)
}

object DefaultJsServerSystemConf extends JsServerSystemConf(js.Array())
