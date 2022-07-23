package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.model.document.DeclaresModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PathSuggestor {
  def suggest(): Future[Seq[RawSuggestion]]

  protected def buildSuggestions(names: Seq[String], prefix: String): Seq[RawSuggestion] = {
    val prev =
      if (prefix.endsWith("/")) prefix
      else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
      else prefix + "/"
    names.map(n => RawSuggestion(s"$prev$n", isAKey = false))
  }
}

object PathSuggestor {
  def apply(fullUrl: String, prefix: String, alsConfiguration: ALSConfigurationState): Future[PathSuggestor] = {
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
        case Some(schema: DeclaresModel) => DeclarablePathSuggestor(schema, prefix)
        case None                        => PathNavigation(fileUri, navPath, prefix, alsConfiguration)
      }
    }
  }
}
