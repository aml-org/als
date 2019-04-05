package org.mulesoft.als.server

import org.mulesoft.lsp.ConfigType

trait ConfigHandler[-C, +S] {
  val `type`: ConfigType[C, S]

  def applyConfig(config: Option[C]): S
}
