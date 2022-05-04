package org.mulesoft.als.suggestions.plugins.aml

import amf.core.internal.plugins.syntax.SyamlSyntaxParsePlugin
import org.mulesoft.als.common.{DirectoryResolver, YPartBranch}
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
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
    if (ramlOrJsonInclusion(params)) {
      resolveInclusion(
        params.baseUnit.location().getOrElse(""),
        params.directoryResolver,
        params.prefix,
        params.rootUri,
        params.alsConfigurationState
      )
    } else emptySuggestion

  private def ramlOrJsonInclusion(params: AmlCompletionRequest) = {
    params.astPartBranch match {
      case yPartBranch: YPartBranch =>
        DialectKnowledge.isRamlInclusion(yPartBranch, params.nodeDialect) || DialectKnowledge.isJsonInclusion(
          yPartBranch,
          params.nodeDialect
        )
      case _ => false
    }
  }
  def resolveInclusion(
      actualLocation: String,
      directoryResolver: DirectoryResolver,
      prefix: String,
      rootLocation: Option[String],
      alsConfiguration: ALSConfigurationState
  ): Future[Seq[RawSuggestion]] = {
    val baseLocation: String =
      if (prefix.startsWith("/")) rootLocation.getOrElse(actualLocation)
      else actualLocation
    val fullPath = baseLocation.toPath(alsConfiguration.platform)
    val baseDir  = extractPath(fullPath) // root path for file

    val relativePath = extractPath(prefix)
    val fullURI =
      s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}".toAmfUri(alsConfiguration.platform)

    if (!prefix.startsWith("#"))
      if (fullURI.contains("#") && !fullURI.startsWith("#"))
        PathNavigation(fullURI, prefix, alsConfiguration).suggest()
      else
        FilesEnumeration(
          directoryResolver,
          alsConfiguration,
          actualLocation.toPath(alsConfiguration.platform),
          relativePath
        )
          .filesIn(fullURI)
    else emptySuggestion

  }

}

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
      (SyamlSyntaxParsePlugin.mediaTypes)
        .contains(mime)
    )
      Some(SyamlSyntaxParsePlugin)
    else None
}
