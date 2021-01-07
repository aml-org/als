package org.mulesoft.als.server.feature.configuration

import org.mulesoft.lsp.configuration.FormattingOptions

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}

case class UpdateConfigurationParams(updateFormatOptionsParams: Option[Map[String, FormattingOptions]],
                                     genericOptions: Map[String, Any] = Map.empty,
                                     disableTemplates: Boolean = false)

@JSExportAll
@JSExport
object GenericOptionKeys {
  val KeepTokens = "keepTokens"
}
