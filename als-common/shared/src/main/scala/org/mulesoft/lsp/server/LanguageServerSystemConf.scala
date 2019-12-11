package org.mulesoft.lsp.server

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment

trait LanguageServerSystemConf {
  def platform: Platform
  def environment: Environment
}

object DefaultServerSystemConf extends LanguageServerSystemConf with PlatformSecrets {
  override def environment = Environment()
}
