package org.mulesoft.als.suggestions.plugins.aml

import amf.core.internal.utils.UriUtils
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.pathnavigation.{FileCompletionFilters, FileWithType, PathCompletion, PredicateParams}
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FilesEnumeration(
    directoryResolver: DirectoryResolver,
    alsConfiguration: ALSConfigurationState,
    actual: String,
    relativePath: String
) {

  def filesIn(fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver.isDirectory(UriUtils.resolvePath(fullURI)).flatMap { isDir =>
      if (isDir) listDirectory(fullURI)
      else Future.successful(Nil)
    }

  private def listDirectory(fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver
      .readDir(UriUtils.resolvePath(fullURI))
      .flatMap(withIsDir(_, fullURI))
      .map(s => {
        s.filter(fileWithType => FileCompletionFilters.filter(PredicateParams(fullURI, actual, fileWithType, alsConfiguration.platform)))
          .map(t => if (t.isDirectory) s"${t.file}/" else t.file)
          .map(toRawSuggestion)
      })

  private def withIsDir(files: Seq[String], fullUri: String): Future[Seq[FileWithType]] =
    Future.sequence {
      files.map(file =>
        directoryResolver
          .isDirectory(
            UriUtils.resolvePath(
              s"${fullUri.toPath(alsConfiguration.platform)}$file".toAmfUri(alsConfiguration.platform)
            )
          )
          .map(isDir => FileWithType(file, isDir))
      )
    }

  private def toRawSuggestion(file: String) =
    RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil)
}
