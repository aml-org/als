package org.mulesoft.lsp.server

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}

trait LanguageServerSystemConf {
  def platform: Platform

  def environment: Environment

  def directoryResolver: DirectoryResolver
}

object DefaultServerSystemConf extends LanguageServerSystemConf with PlatformSecrets {
  override def environment = Environment()

  override def directoryResolver = new PlatformDirectoryResolver(platform)
}
