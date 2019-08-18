package org.mulesoft.als.suggestions.plugins.aml

import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.{DirectoryResolver, FileUtils, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
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
      resolveInclusion(params.baseUnit.location().getOrElse(""),
                       params.platform,
                       params.prefix,
                       params.directoryResolver)
    } else emptySuggestion

  def resolveInclusion(actualLocation: String,
                       platform: Platform,
                       prefix: String,
                       directoryResolver: DirectoryResolver): Future[Seq[RawSuggestion]] = {
    val baseDir      = extractPath(FileUtils.getPath(actualLocation, platform)) // root path for file
    val relativePath = extractPath(prefix) // already written part of the path
    val fullURI      = FileUtils.getEncodedUri(s"$baseDir$relativePath", platform)
    val actual       = actualLocation.stripPrefix(fullURI)

    doIfDirectory(directoryResolver, fullURI)(
      listDirectory(directoryResolver, platform, relativePath, actual, fullURI))
  }
  private def doIfDirectory(directoryResolver: DirectoryResolver, fullURI: String)(
      doIt: => Future[Seq[RawSuggestion]]): Future[Seq[RawSuggestion]] =
    directoryResolver.isDirectory(fullURI).flatMap(if (_) doIt else emptySuggestion)

  private def listDirectory(directoryResolver: DirectoryResolver,
                            platform: Platform,
                            relativePath: String,
                            actual: String,
                            fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver
      .readDir(fullURI)
      .flatMap(withIsDir(_, fullURI, directoryResolver, platform))
      .map(s => {
        val filtered = s
          .filter(tuple => tuple._1 != actual && (tuple._2 || supportedMime(tuple._1, platform)))
          .map(t => if (t._2) s"${t._1}/" else t._1)
        toSuggestions(relativePath, filtered)
      })

  private def withIsDir(files: Seq[String],
                        fullUri: String,
                        directoryResolver: DirectoryResolver,
                        platform: Platform): Future[Seq[(String, Boolean)]] =
    Future.sequence {
      files.map(
        file =>
          directoryResolver
            .isDirectory(FileUtils.getEncodedUri(s"${FileUtils.getPath(fullUri, platform)}$file", platform))
            .map(isDir => (file, isDir)))
    }

  private def toSuggestions(relativePath: String, files: Seq[String]): Seq[RawSuggestion] =
    files.map(toRawSuggestion(relativePath, _))

  private def supportedMime(file: String, platform: Platform): Boolean =
    platform
      .extension(file)
      .flatMap(platform.mimeFromExtension)
      .exists(mime => AMFPluginsRegistry.syntaxPluginForMediaType(mime).isDefined)

  private def toRawSuggestion(relativePath: String, file: String) =
    RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil, isKey = false, "")
}
