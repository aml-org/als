package org.mulesoft.als.suggestions.plugins.aml

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

  // just take directory paths
  def extractPath(parts: String): String =
    if (parts.indexOf('/') > 0)
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
                            fullURI: String): Future[Seq[RawSuggestion]] = {
    params.directoryResolver.readDir(fullURI).map(toSuggestions(relativePath, _))
  }

  private def toSuggestions(relativePath: String, files: Seq[String]): Seq[RawSuggestion] = {
    files.map(toRawSuggestion(relativePath, _))
  }

  private def toRawSuggestion(relativePath: String, file: String) = {
    RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil, isKey = false, "")
  }
}
