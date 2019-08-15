package org.mulesoft.als.suggestions.plugins.aml

import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.{DirectoryResolver, FileUtils, YPartBranch}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLPathCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLPathCompletionPlugin"

  private def isStyleValue(style: String, yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.isValue && Option(dialect.documents()).forall(d =>
      d.referenceStyle().is(style) || d.referenceStyle().isNullOrEmpty)

  private def isRamlInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    isStyleValue(ReferenceStyles.RAML, yPartBranch, dialect) &&
      yPartBranch.hasIncludeTag

  private def isJsonInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    isStyleValue(ReferenceStyles.JSONSCHEMA, yPartBranch, dialect) &&
      yPartBranch.parentEntry.exists(p => p.key.asScalar.exists(_.text == "$ref"))

  // exclude file name
  def extractPath(parts: String): String =
    if (parts.lastIndexOf('/') >= 0)
      parts.substring(0, parts.lastIndexOf('/') + 1)
    else ""

  // TODO: When project is implemented, baseDir should depend on '/' prefix (to define if root is main file or current one)
  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
    if (isRamlInclusion(params.yPartBranch, params.dialect) || isJsonInclusion(params.yPartBranch, params.dialect)) {
      val baseDir      = extractPath(FileUtils.getPath(params.baseUnit.location().getOrElse(""), params.platform)) // root path for file
      val relativePath = extractPath(params.prefix) // already written part of the path
      val fullURI      = FileUtils.getEncodedUri(s"$baseDir$relativePath", params.platform)

      doIfDirectory(params.directoryResolver, fullURI)(listDirectory(params, relativePath, fullURI))
    } else emptySuggestion

  private def doIfDirectory(directoryResolver: DirectoryResolver, fullURI: String)(
      doIt: => Future[Seq[RawSuggestion]]): Future[Seq[RawSuggestion]] =
    directoryResolver.isDirectory(fullURI).flatMap(if (_) doIt else emptySuggestion)

  private def listDirectory(params: AMLCompletionParams,
                            relativePath: String,
                            fullURI: String): Future[Seq[RawSuggestion]] =
    params.directoryResolver
      .readDir(fullURI)
      .flatMap(withIsDir(_, fullURI, params.directoryResolver, params.platform))
      .map(s => {
        val filtered = s
          .filter(tuple => tuple._2 || supportedMime(tuple._1, params.platform))
          .map(t => if (t._2) s"${t._1}/" else t._1)
        toSuggestions(relativePath, filtered, params.platform)
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

  private def toSuggestions(relativePath: String, files: Seq[String], platform: Platform): Seq[RawSuggestion] =
    files.map(toRawSuggestion(relativePath, _))

  private def supportedMime(file: String, platform: Platform): Boolean =
    platform
      .extension(file)
      .flatMap(platform.mimeFromExtension)
      .exists(mime => AMFPluginsRegistry.syntaxPluginForMediaType(mime).isDefined)

  private def toRawSuggestion(relativePath: String, file: String) =
    RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil, isKey = false, "")
}
