package org.mulesoft.als.server

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.configuration.ClientDirectoryResolver
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidatorBuilder
import org.mulesoft.als.server.protocol.LanguageServer

trait ClientLanguageServerFactory[SerializeProps, ClientLogger, ClientAMFPlugin, ClientAMFValidator] {
  protected val clientNotifier: ClientNotifier
  protected val innerFactory = new LanguageServerFactory(clientNotifier)

  def convertLogger(logger: ClientLogger): Logger
  def convertDirectoryResolver(directoryResolver: ClientDirectoryResolver): DirectoryResolver
  def convertValidator(validator: ClientAMFValidator): AMFOpaValidatorBuilder
  def convertResourceLoaders(rl: Seq[ClientResourceLoader]): Seq[ResourceLoader]
  def convertPlugins(plugins: Seq[ClientAMFPlugin]): Seq[AMFShapePayloadValidationPlugin]

  def withSerializationProps(serializationProps: SerializationProps[SerializeProps]): this.type = {
    innerFactory.withSerializationProps(serializationProps)
    this
  }

  def withResourceLoaders(rl: Seq[ClientResourceLoader]): this.type = {
    innerFactory.withResourceLoaders(convertResourceLoaders(rl))
    this
  }

  def withLogger(logger: Logger): this.type = {
    innerFactory.withLogger(logger)
    this
  }

  // if named `withLogger` it will clash with `withLogger` in JVM distribution as ClientLogger type is in fact Logger
  def withClientLogger(logger: ClientLogger): this.type =
    withLogger(convertLogger(logger))

  def withNotificationKind(notificationsKind: DiagnosticNotificationsKind): this.type = {
    innerFactory.withNotificationKind(notificationsKind)
    this
  }

  def withNotificationKind(notificationsKind: Option[DiagnosticNotificationsKind]): this.type = {
    notificationsKind.foreach(innerFactory.withNotificationKind)
    this
  }

  def withDirectoryResolver(dr: ClientDirectoryResolver): this.type = {
    innerFactory.withDirectoryResolver(convertDirectoryResolver(dr))
    this
  }

  def withAmfPlugins(plugins: Seq[AMFShapePayloadValidationPlugin]): this.type = {
    innerFactory.withAmfPlugins(plugins)
    this
  }

  def withAmfCustomValidator(validator: ClientAMFValidator): this.type = {
    innerFactory.withAmfCustomValidator(convertValidator(validator))
    this
  }

  def build(): LanguageServer = innerFactory.build()
}
