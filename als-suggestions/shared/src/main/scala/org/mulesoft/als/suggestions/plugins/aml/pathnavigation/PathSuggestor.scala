package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.model.document.DeclaresModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PathSuggestor {
  def suggest(): Future[Seq[RawSuggestion]]

  protected def buildSuggestions(names: Seq[String], prefix: String): Seq[RawSuggestion] =
    names.map(n => RawSuggestion(s"${prevFromPrefix(prefix)}$n", isAKey = false))

  protected def prevFromPrefix(prefix: String): String =
    if (prefix.endsWith("/")) prefix
    else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
    else prefix + "/"
}

case class PathSuggestorHashtag(names: Seq[String], prefix: String) extends PathSuggestor {
  override protected def prevFromPrefix(prefix: String): String =
    s"$prefix#/"

  override def suggest(): Future[Seq[RawSuggestion]] = Future.successful(buildSuggestions(names, prefix))
}

object PathSuggestor {
  def apply(
      fullUrl: String,
      prefix: String,
      alsConfiguration: ALSConfigurationState,
      targetClass: Option[String]
  ): Future[PathSuggestor] = {
    val (fileUri, navPath, containsHashtag) =
      splitFullUrl(fullUrl, prefix)

    for {
      cached <-
        try {
          alsConfiguration.cache
            .fetch(fileUri)
            .map(f => Some(f.content))
            .recoverWith { case _ =>
              Future.successful(None)
            }
        } catch {
          case _: Exception =>
            Future.successful(None)
        }
    } yield {
      cached match {
        case Some(schema: DeclaresModel) => DeclarablePathSuggestor(schema, prefix, targetClass)
        case _                        => PathNavigation(fileUri, navPath, prefix, alsConfiguration, containsHashtag)
      }
    }
  }

  private def mergeStrings(str1: String, str2: String): String = {
    val maxOverlap   = str1.length.min(str2.length)
    val overlap      = (1 to maxOverlap).find(i => str1.endsWith(str2.take(i)))
    val commonSuffix = overlap.map(str2.take).getOrElse("")
    str1.stripSuffix(commonSuffix) + str2
  }

  def splitFullUrl(fullUrl: String, prefix: String): (String, String, Boolean) = {
    val url = mergeStrings(fullUrl, prefix)

    url.split("#").toList match {
      case head :: tail => (head, tail.headOption.getOrElse(""), url.contains("#"))
      case _            => ("", "", url.contains("#"))
    }
  }
}
