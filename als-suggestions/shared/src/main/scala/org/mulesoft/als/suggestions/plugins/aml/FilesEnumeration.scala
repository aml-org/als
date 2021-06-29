package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FilesEnumeration(directoryResolver: DirectoryResolver,
                            override implicit val amfConfiguration: AmfConfigurationWrapper,
                            actual: String,
                            relativePath: String)
    extends PathCompletion {

  def filesIn(fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver.isDirectory(amfConfiguration.platform.resolvePath(fullURI)).flatMap { isDir =>
      if (isDir) listDirectory(fullURI)
      else Future.successful(Nil)
    }

  private def listDirectory(fullURI: String): Future[Seq[RawSuggestion]] =
    directoryResolver
      .readDir(amfConfiguration.platform.resolvePath(fullURI))
      .flatMap(withIsDir(_, fullURI))
      .map(s => {
        s.filter(tuple =>
            s"${fullURI.toPath(amfConfiguration.platform)}${tuple._1}" != actual && (tuple._2 || supportedExtension(
              tuple._1)))
          .map(t => if (t._2) s"${t._1}/" else t._1)
          .map(toRawSuggestion)
      })

  private def withIsDir(files: Seq[String], fullUri: String): Future[Seq[(String, Boolean)]] =
    Future.sequence {
      files.map(
        file =>
          directoryResolver
            .isDirectory(amfConfiguration.platform.resolvePath(
              s"${fullUri.toPath(amfConfiguration.platform)}$file".toAmfUri(amfConfiguration.platform)))
            .map(isDir => (file, isDir)))
    }

  private def toRawSuggestion(file: String) =
    RawSuggestion(s"$relativePath$file", s"$relativePath$file", "Path suggestion", Nil)
}
