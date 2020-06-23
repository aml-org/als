package org.mulesoft.als.server.feature.configuration

import org.mulesoft.als.configuration.AlsFormattingOptions

case class UpdateConfigurationParams(updateFormatOptionsParams: Option[Map[String, AlsFormattingOptions]])
