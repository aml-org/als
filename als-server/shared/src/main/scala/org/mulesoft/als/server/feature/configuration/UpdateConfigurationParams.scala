package org.mulesoft.als.server.feature.configuration

import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.lsp.configuration.FormattingOptions

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

case class UpdateConfigurationParams(
    updateFormatOptionsParams: Option[Map[String, FormattingOptions]],
    genericOptions: Map[String, Any] = Map.empty,
    templateType: String = TemplateTypes.FULL,
    prettyPrintSerialization: Boolean = false
)

@JSExportAll
@JSExportTopLevel("GenericOptionKeys")
object GenericOptionKeys {
  val KeepTokens = "keepTokens"
}
