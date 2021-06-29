package org.mulesoft.als.configuration

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class JsServerSystemConf(clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                              clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver)
    extends PlatformSecrets {

  private val loaders: Seq[ResourceLoader] =
    if (clientLoaders.isEmpty) platform.loaders()
    else clientLoaders.map(ResourceLoaderConverter.internalResourceLoader).toSeq

  val amfConfiguration: AmfConfigurationWrapper = AmfConfigurationWrapper(loaders)

  def directoryResolver: DirectoryResolver =
    DirectoryResolverAdapter.convert(clientDirResolver)
}

object DefaultJsServerSystemConf extends JsServerSystemConf(js.Array())
