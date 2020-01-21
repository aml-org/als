package org.mulesoft.lsp.server

import amf.client.remote.Content
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}

import scala.concurrent.Future

trait LanguageServerSystemConf {
  def resolve(uri: String): Future[Content] = platform.resolve(uri, environment)

  def platform: Platform

  def environment: Environment

  def directoryResolver: DirectoryResolver
}

case class LanguageServerEnvironmentInstance(platform: Platform,
                                             environment: Environment,
                                             directoryResolver: DirectoryResolver)
    extends LanguageServerSystemConf

object DefaultServerSystemConf extends LanguageServerSystemConf with PlatformSecrets {
  override def environment = Environment()

  override def directoryResolver = new PlatformDirectoryResolver(platform)
}
