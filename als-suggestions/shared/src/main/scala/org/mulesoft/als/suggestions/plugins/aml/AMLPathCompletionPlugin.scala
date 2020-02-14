package org.mulesoft.als.suggestions.plugins.aml

import amf.client.plugins.AMFSyntaxPlugin
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, FileUtils}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfmanager.dialect.DialectKnowledge

import scala.concurrent.Future

object AMLPathCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLPathCompletionPlugin"

  // exclude file name
  def extractPath(parts: String): String =
    if (parts.lastIndexOf('/') >= 0)
      parts.substring(0, parts.lastIndexOf('/') + 1)
    else ""

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (DialectKnowledge.isRamlInclusion(params.yPartBranch, params.actualDialect) || DialectKnowledge.isJsonInclusion(
          params.yPartBranch,
          params.actualDialect)) {
      resolveInclusion(params.baseUnit.location().getOrElse(""),
                       params.environment,
                       params.platform,
                       params.directoryResolver,
                       params.prefix,
                       params.rootUri)
    } else emptySuggestion

  def resolveInclusion(actualLocation: String,
                       environment: Environment,
                       platform: Platform,
                       directoryResolver: DirectoryResolver,
                       prefix: String,
                       rootLocation: Option[String]): Future[Seq[RawSuggestion]] = {
    val baseLocation: String =
      if (prefix.startsWith("/")) rootLocation.getOrElse(actualLocation)
      else actualLocation
    val fullPath = FileUtils.getPath(baseLocation, platform)
    val baseDir  = extractPath(fullPath) // root path for file

    val relativePath = extractPath(prefix)
    val fullURI =
      FileUtils.getEncodedUri(s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}", platform)
    val actual = fullPath.stripPrefix(baseDir)

    if (!prefix.startsWith("#"))
      if (fullURI.contains("#") && !fullURI.startsWith("#"))
        PathNavigation(fullURI, platform, environment, prefix).suggest()
      else
        FilesEnumeration(directoryResolver, platform, actual, relativePath)
          .filesIn(fullURI)
    else emptySuggestion

  }

}

trait PathCompletion {
  val platform: Platform

  def supportedExtension(file: String): Boolean =
    platform
      .extension(file)
      .flatMap(platform.mimeFromExtension)
      .exists(pluginForMime(_).isDefined)

  def pluginForMime(mime: String): Option[AMFSyntaxPlugin] =
    AMFPluginsRegistry.syntaxPluginForMediaType(mime)
}
