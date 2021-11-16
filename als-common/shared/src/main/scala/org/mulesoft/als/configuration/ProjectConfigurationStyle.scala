package org.mulesoft.als.configuration

import org.mulesoft.als.configuration.ConfigurationStyle.ConfigurationStyle

case class ProjectConfigurationStyle(style: ConfigurationStyle)

@deprecated("Deprecated, will be removed with ALS 5, file-style will no longer be supported", "4.2.1")
object ConfigurationStyle extends Enumeration {
  type ConfigurationStyle = Value
  val COMMAND: ConfigurationStyle = Value(0, "command")
  val FILE: ConfigurationStyle    = Value(1, "file")

  def apply(s: String): ConfigurationStyle = {
    try {
      withName(s)
    } catch {
      case _: NoSuchElementException => FILE
    }
  }
}

object DefaultProjectConfigurationStyle extends ProjectConfigurationStyle(ConfigurationStyle.FILE)
