package org.mulesoft.als.server.client.platform

import amf.core.client.platform.resource.ClientResourceLoader
import amf.custom.validation.client.platform.CustomValidator
import amf.custom.validation.internal.convert.AmfCustomValidatorClientConverters.CustomValidatorConverter
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
  protected val _internal: LanguageServerFactory = new LanguageServerFactory(clientNotifier)

  def withSerializationProps(serializationProps: SerializationProps[_]): this.type = {
    _internal.withSerializationProps(serializationProps)
    this
  }

  def withResourceLoaders(rl: ClientList[ClientResourceLoader]): this.type = {
    _internal.withResourceLoaders(rl.asInternal)
    this
  }

  def withAdditionalResourceLoader(rl: ClientResourceLoader): this.type = {
    _internal.withAdditionalResourceLoader(rl)
    this
  }

  def withAdditionalResourceLoaders(rl: ClientList[ClientResourceLoader]): this.type = {
    _internal.withAdditionalResourceLoaders(rl.asInternal)
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

  def withAmfPlugins(plugins: ClientList[ClientAMFPlugin]): this.type = {
    _internal.withAmfPlugins(plugins.asInternal)
    this
  }

  def withAmfCustomValidator(validator: CustomValidator): this.type = {
    _internal.withAmfCustomValidator(CustomValidatorConverter.asInternal(validator))
    this
  }

  def withNewCachingLogic(p: Boolean): this.type = {
    _internal.withNewCachingLogic(p)
    this
  }

  def build(): LanguageServer = _internal.build()
}
