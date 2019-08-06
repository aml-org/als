package org.mulesoft.lsp

trait ConfigHandler[-C, +S] {
  val `type`: ConfigType[C, S]

  def applyConfig(config: Option[C]): S
}
