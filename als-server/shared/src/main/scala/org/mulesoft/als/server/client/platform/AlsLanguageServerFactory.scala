package org.mulesoft.als.server.client.platform

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import org.mulesoft.als.configuration.ClientDirectoryResolver
import org.mulesoft.als.server.ALSConverters._
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.client.scala.LanguageServerFactory
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.protocol.LanguageServer

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("AlsLanguageServerFactory")
class AlsLanguageServerFactory(clientNotifier: ClientNotifier) {
  private val _internal: LanguageServerFactory = new LanguageServerFactory(clientNotifier)

  def withSerializationProps(serializationProps: SerializationProps[_]): this.type = {
    _internal.withSerializationProps(serializationProps)
    this
  }

  def withResourceLoaders(rl: ClientList[ClientResourceLoader]): this.type = {
    _internal.withResourceLoaders(rl.map(ClientResourceLoaderConverter.asInternal))
    this
  }

  def withLogger(logger: ClientLogger): this.type = {
    _internal.withLogger(asInternal(logger))
    this
  }

  def withNotificationKind(notificationsKind: DiagnosticNotificationsKind): this.type = {
    _internal.withNotificationKind(notificationsKind)
    this
  }

  def withNotificationKind(notificationsKind: Option[DiagnosticNotificationsKind]): this.type = {
    notificationsKind.foreach(_internal.withNotificationKind)
    this
  }

  def withDirectoryResolver(dr: ClientDirectoryResolver): this.type = {
    _internal.withDirectoryResolver(dr)
    this
  }

  def withAmfPlugins(plugins: ClientList[AMFShapePayloadValidationPlugin]): this.type = {
    _internal.withAmfPlugins(plugins)
    this
  }

  def withAmfCustomValidator(validator: ClientAMFValidator): this.type = {
    _internal.withAmfCustomValidator(asInternal(validator))
    this
  }

  def build(): LanguageServer = _internal.build()
}
