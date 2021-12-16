package org.mulesoft.als.server.lsp4j

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.CoreClientConverters.ClientList
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.configuration.{ClientDirectoryResolver, DirectoryResolverAdapter, ResourceLoaderConverter}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.ClientLanguageServerFactory
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.diagnostic.custom.{AMFOpaValidator, AMFOpaValidatorBuilder}

import java.io.StringWriter
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class JvmLanguageServerFactory(override protected val clientNotifier: ClientNotifier)
    extends JvmPlatformLanguageServerFactory {

  override def convertLogger(logger: Logger): Logger = logger

  override def convertDirectoryResolver(directoryResolver: ClientDirectoryResolver): DirectoryResolver =
    DirectoryResolverAdapter.convert(directoryResolver)

  override def convertValidator(validator: AMFOpaValidator): AMFOpaValidatorBuilder =
    (logger: Logger) => validator

  override def convertResourceLoaders(rl: Seq[ClientResourceLoader]): Seq[ResourceLoader] =
    rl.map(ResourceLoaderConverter.internalResourceLoader)

  override def convertPlugins(plugins: Seq[AMFShapePayloadValidationPlugin]): Seq[AMFShapePayloadValidationPlugin] =
    plugins
}

trait JvmPlatformLanguageServerFactory
    extends ClientLanguageServerFactory[StringWriter, Logger, AMFShapePayloadValidationPlugin, AMFOpaValidator] {

  def withResourceLoaders(rls: ClientList[ClientResourceLoader]): this.type = {
    withResourceLoaders(rls.toSeq)
    this
  }

  def withAmfPlugins(plugins: ClientList[AMFShapePayloadValidationPlugin]): this.type = {
    withAmfPlugins(convertPlugins(plugins.toSeq))
    this
  }
}
