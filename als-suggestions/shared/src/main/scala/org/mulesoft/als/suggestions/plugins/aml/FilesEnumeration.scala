package org.mulesoft.als.suggestions.plugins.aml

import amf.core.internal.utils.UriUtils
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.pathnavigation.PathCompletion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FilesEnumeration(
    directoryResolver: DirectoryResolver,
    override implicit val alsConfiguration: ALSConfigurationState,
    actual: String,
    relativePath: String
) extends PathCompletion {

  def filesIn(fullURI: String, prefix: String): Future[Seq[RawSuggestion]] = {
    val fixedURI = removePrefixIfNeeded(fullURI, prefix)
    directoryResolver.isDirectory(UriUtils.resolvePath(fixedURI)).flatMap { isDir =>
      if (isDir) listDirectory(fixedURI)
      else Future.successful(Nil)
    }
  }

  private def removePrefixIfNeeded(fullURI: String, prefix: String): String =
    if (!(prefix.isEmpty || prefix.contains("/")))
      fullURI.substring(0, fullURI.lastIndexOf(prefix))
    else fullURI

  private def listDirectory(fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver
      .readDir(UriUtils.resolvePath(fullURI))
      .flatMap(withIsDir(_, fullURI))
      .map(s => {
        s.filter(tuple =>
          s"${fullURI.toPath(alsConfiguration.platform)}${tuple._1}" != actual && (tuple._2 || supportedExtension(
            tuple._1
          ))
        ).map(t => if (t._2) s"${t._1}/" else t._1)
          .map(toRawSuggestion)
      })

  private def withIsDir(files: Seq[String], fullUri: String): Future[Seq[(String, Boolean)]] =
    Future.sequence {
      files.map(file =>
        directoryResolver
          .isDirectory(
            UriUtils.resolvePath(
              s"${fullUri.toPath(alsConfiguration.platform)}$file".toAmfUri(alsConfiguration.platform)
            )
          )
          .map(isDir => (file, isDir))
      )
    }

  private def toRawSuggestion(file: String) = {
    if (relativePath.endsWith("/"))
      RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil)
    else
      RawSuggestion(s"$file", s"$file", "Path suggestion", Nil)
  }
}
