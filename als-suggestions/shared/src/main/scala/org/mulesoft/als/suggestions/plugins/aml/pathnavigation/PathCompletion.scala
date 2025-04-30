package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.internal.plugins.syntax.SyamlSyntaxParsePlugin
import amf.core.internal.remote.Platform
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

trait PathCompletion {
  val exceptions = Seq("xml", "xsd", "md", "avsc")

  def supportedExtension(file: String, platform: Platform): Boolean = {
    val maybeExtension = platform
      .extension(file)
    maybeExtension
      .flatMap(ext => platform.mimeFromExtension(ext))
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
