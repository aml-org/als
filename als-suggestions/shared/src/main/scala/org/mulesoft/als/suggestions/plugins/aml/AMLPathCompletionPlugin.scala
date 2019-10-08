package org.mulesoft.als.suggestions.plugins.aml

import amf.client.plugins.AMFSyntaxPlugin
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.{FileUtils, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, CompletionEnvironment}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object AMLPathCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLPathCompletionPlugin"

  private def isStyleValue(style: String, yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    Option(dialect.documents()).forall(d => d.referenceStyle().is(style) || d.referenceStyle().isNullOrEmpty)

  private def isRamlInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.hasIncludeTag && isStyleValue(ReferenceStyles.RAML, yPartBranch, dialect)

  private def isJsonInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.isValue && isStyleValue(ReferenceStyles.JSONSCHEMA, yPartBranch, dialect) &&
      yPartBranch.parentEntry.exists(p => p.key.asScalar.exists(_.text == "$ref"))

  // exclude file name
  def extractPath(parts: String): String =
    if (parts.lastIndexOf('/') >= 0)
      parts.substring(0, parts.lastIndexOf('/') + 1)
    else ""

  // TODO: When project is implemented, baseDir should depend on '/' prefix (to define if root is main file or current one)
  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isRamlInclusion(params.yPartBranch, params.actualDialect) || isJsonInclusion(params.yPartBranch,
                                                                                     params.actualDialect)) {
      resolveInclusion(params.baseUnit.location().getOrElse(""), params.env, params.prefix)
    } else emptySuggestion

  def resolveInclusion(actualLocation: String,
                       env: CompletionEnvironment,
                       prefix: String): Future[Seq[RawSuggestion]] = {
    val fullPath     = FileUtils.getPath(actualLocation, env.platform)
    val baseDir      = extractPath(fullPath) // root path for file
    val relativePath = extractPath(prefix)
    val fullURI =
      FileUtils.getEncodedUri(s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}", env.platform)
    val actual = fullPath.stripPrefix(baseDir)

    if (!prefix.startsWith("#"))
      if (fullURI.contains("#") && !fullURI.startsWith("#"))
        PathNavigation(fullURI, env.platform, env.env, prefix).suggest()
      else FilesEnumeration(env.directoryResolver, env.platform, actual, relativePath).filesIn(fullURI)
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

  def pluginForMime(mime: String): Option[AMFSyntaxPlugin] = AMFPluginsRegistry.syntaxPluginForMediaType(mime)
}
