package org.mulesoft.als.server.feature.configuration

import org.mulesoft.als.configuration.AlsFormattingOptions

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}

case class UpdateConfigurationParams(updateFormatOptionsParams: Option[Map[String, AlsFormattingOptions]],
                                     genericOptions: Map[String, Any] = Map.empty)

@JSExportAll
@JSExport
object GenericOptionKeys {
  val KeepTokens = "keepTokens"
}
