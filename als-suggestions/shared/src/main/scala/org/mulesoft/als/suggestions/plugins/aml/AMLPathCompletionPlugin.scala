package org.mulesoft.als.suggestions.plugins.aml

import amf.client.plugins.AMFSyntaxPlugin
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfInstance
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
                       params.environment,
                       params.platform,
                       params.directoryResolver,
                       params.prefix,
                       params.rootUri,
                       params.amfInstance)
    } else emptySuggestion

  def resolveInclusion(actualLocation: String,
                       environment: Environment,
                       platform: Platform,
                       directoryResolver: DirectoryResolver,
                       prefix: String,
                       rootLocation: Option[String],
                       amfInstance: AmfInstance): Future[Seq[RawSuggestion]] = {
    val baseLocation: String =
      if (prefix.startsWith("/")) rootLocation.getOrElse(actualLocation)
      else actualLocation
    val fullPath = baseLocation.toPath(platform)
    val baseDir  = extractPath(fullPath) // root path for file

    val relativePath = extractPath(prefix)
    val fullURI =
      s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}".toAmfUri(platform)

    if (!prefix.startsWith("#"))
      if (fullURI.contains("#") && !fullURI.startsWith("#"))
        PathNavigation(fullURI, platform, environment, prefix, amfInstance).suggest()
      else
        FilesEnumeration(directoryResolver, platform, actualLocation.toPath(platform), relativePath)
          .filesIn(fullURI)
    else emptySuggestion

  }

}

trait PathCompletion {
  val platform: Platform

  val exceptions = Seq("xml", "xsd", "md")

  def supportedExtension(file: String): Boolean = {
    val maybeExtension = platform
      .extension(file)
    maybeExtension
      .flatMap(ext => platform.mimeFromExtension(ext))
      .exists(pluginForMime(_).isDefined) ||
    maybeExtension.exists(exceptions.contains)
  }

  def pluginForMime(mime: String): Option[AMFSyntaxPlugin] =
    AMFPluginsRegistry.syntaxPluginForMediaType(mime)
}
