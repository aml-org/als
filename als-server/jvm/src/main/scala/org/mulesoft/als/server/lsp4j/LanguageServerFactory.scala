package org.mulesoft.als.server.lsp4j

import amf.client.convert.CoreClientConverters._
import amf.client.environment.Environment
import amf.client.plugins.AMFPlugin
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.{Environment => InternalEnvironment}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, PARSING_BEFORE}
import org.mulesoft.als.server.{EmptyJvmSerializationProps, JvmSerializationProps, LanguageServerBuilder}
import org.mulesoft.lsp.server.{AmfInstance, LanguageServer}

import scala.collection.JavaConverters._

// todo: standarize in one only converter (js and jvm) with generics
class LanguageServerFactory(clientNotifier: ClientNotifier) extends PlatformSecrets {

  private var serialization: JvmSerializationProps           = EmptyJvmSerializationProps
  private var logger: Logger                                 = PrintLnLogger
  private var givenPlatform: Platform                        = platform
  private var environment: InternalEnvironment               = InternalEnvironment()
  private var notificationsKind: DiagnosticNotificationsKind = PARSING_BEFORE
  private var amfPlugins: java.util.List[AMFPlugin]          = new java.util.ArrayList()

  def withSerializationProps(serializationProps: JvmSerializationProps): LanguageServerFactory = {
    serialization = serializationProps
    this
  }

  def withLogger(logger: Logger): LanguageServerFactory = {
    this.logger = logger
    this
  }

  def withGivenPlatform(givenPlatform: Platform): LanguageServerFactory = {
    this.givenPlatform = givenPlatform
    this
  }

  def withEnvironment(environment: Environment): LanguageServerFactory = {
    this.environment = convertEnv(environment)
    this
  }

  def withNotificationKind(notificationsKind: DiagnosticNotificationsKind): LanguageServerFactory = {
    this.notificationsKind = notificationsKind
    this
  }

  def withAmfPlugins(amfPlugins: java.util.List[AMFPlugin]): LanguageServerFactory = {
    this.amfPlugins = amfPlugins
    this
  }

  private def convertEnv(environment: Environment): InternalEnvironment = {
    val i = InternalEnvironment.empty()
    i.withLoaders(environment.loaders.asInternal)
    // i.withResolver(environment.reference)
    i
  }

  def build(): LanguageServer = {
    val factory = new WorkspaceManagerFactoryBuilder(clientNotifier, logger)
      .withAmfConfiguration(new AmfInstance(amfPlugins.asScala, platform, environment))
    factory.withNotificationKind(notificationsKind) // move to initialization param
    val dm       = factory.diagnosticManager()
    val sm       = factory.serializationManager(serialization)
    val builders = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      new LanguageServerBuilder(builders.documentManager, builders.workspaceManager)
        .addInitializable(builders.workspaceManager)
        .addInitializableModule(dm)
        .addInitializableModule(sm)
        .addRequestModule(builders.cleanDiagnosticManager)
        .addRequestModule(builders.completionManager)
        .addRequestModule(builders.structureManager)
        .addRequestModule(builders.definitionManager)
        .addRequestModule(builders.referenceManager)
        .addRequestModule(builders.documentLinksManager)
        .addInitializable(builders.telemetryManager)
    languageBuilder.build()
  }
}
