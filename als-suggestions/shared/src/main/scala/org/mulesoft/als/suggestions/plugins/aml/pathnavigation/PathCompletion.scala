package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.internal.plugins.syntax.SyamlSyntaxParsePlugin
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

trait PathCompletion {
  val exceptions = Seq("xml", "xsd", "md")
  val alsConfiguration: ALSConfigurationState

  def supportedExtension(file: String): Boolean = {
    val maybeExtension = alsConfiguration.platform
      .extension(file)
    maybeExtension
      .flatMap(ext => alsConfiguration.platform.mimeFromExtension(ext))
      .exists(pluginForMime(_).isDefined) ||
    maybeExtension.exists(exceptions.contains)
  }

  def pluginForMime(mime: String): Option[SyamlSyntaxParsePlugin.type] =
    if (
      SyamlSyntaxParsePlugin.mediaTypes
        .contains(mime)
    )
      Some(SyamlSyntaxParsePlugin)
    else None
}
