package org.mulesoft.als.suggestions.plugins.aml

import amf.core.internal.plugins.syntax.SyamlSyntaxParsePlugin
import amf.core.internal.remote.Mimes
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.amfintegration.dialect.DialectKnowledge

import scala.concurrent.Future

object AMLPathCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLPathCompletionPlugin"

  // exclude file name
  def extractPath(parts: String): String =
    if (parts.lastIndexOf('/') >= 0)
      parts.substring(0, parts.lastIndexOf('/') + 1)
    else ""

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (DialectKnowledge.isRamlInclusion(params.yPartBranch, params.nodeDialect) || DialectKnowledge.isJsonInclusion(
          params.yPartBranch,
          params.nodeDialect)) {
      resolveInclusion(params.baseUnit.location().getOrElse(""),
                       params.directoryResolver,
                       params.prefix,
                       params.rootUri,
                       params.amfConfiguration)
    } else emptySuggestion

  def resolveInclusion(actualLocation: String,
                       directoryResolver: DirectoryResolver,
                       prefix: String,
                       rootLocation: Option[String],
                       amfConfiguration: AmfConfigurationWrapper): Future[Seq[RawSuggestion]] = {
    val baseLocation: String =
      if (prefix.startsWith("/")) rootLocation.getOrElse(actualLocation)
      else actualLocation
    val fullPath = baseLocation.toPath(amfConfiguration.platform)
    val baseDir  = extractPath(fullPath) // root path for file

    val relativePath = extractPath(prefix)
    val fullURI =
      s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}".toAmfUri(amfConfiguration.platform)

    if (!prefix.startsWith("#"))
      if (fullURI.contains("#") && !fullURI.startsWith("#"))
        PathNavigation(fullURI, prefix, amfConfiguration).suggest()
      else
        FilesEnumeration(directoryResolver,
                         amfConfiguration,
                         actualLocation.toPath(amfConfiguration.platform),
                         relativePath)
          .filesIn(fullURI)
    else emptySuggestion

  }

}

trait PathCompletion {
  val exceptions = Seq("xml", "xsd", "md")
  val amfConfiguration: AmfConfigurationWrapper

  def supportedExtension(file: String): Boolean = {
    val maybeExtension = amfConfiguration.platform
      .extension(file)
    maybeExtension
      .flatMap(ext => amfConfiguration.platform.mimeFromExtension(ext))
      .exists(pluginForMime(_).isDefined) ||
    maybeExtension.exists(exceptions.contains)
  }

  def pluginForMime(mime: String): Option[SyamlSyntaxParsePlugin.type] =
    if ((SyamlSyntaxParsePlugin.mediaTypes)
          .contains(mime))
      Some(SyamlSyntaxParsePlugin)
    else None
}
