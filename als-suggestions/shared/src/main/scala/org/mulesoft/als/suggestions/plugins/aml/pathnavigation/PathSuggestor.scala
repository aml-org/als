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

object PathSuggestor {
  def apply(fullUrl: String, prefix: String, alsConfiguration: ALSConfigurationState, targetClass: Option[String]): Future[PathSuggestor] = {
    val (fileUri, navPath) =
      fullUrl.split("#").toList match {
        case head :: tail => (head, tail.headOption.getOrElse(""))
        case _            => ("", "")
      }

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
        case None                        => PathNavigation(fileUri, navPath, prefix, alsConfiguration)
      }
    }
  }
}
