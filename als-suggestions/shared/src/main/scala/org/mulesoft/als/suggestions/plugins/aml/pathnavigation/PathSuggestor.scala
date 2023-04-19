package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PathSuggestor {
  def suggest(): Future[Seq[RawSuggestion]]

  protected def buildSuggestions(names: Seq[String], prefix: String, flag: Boolean = false): Seq[RawSuggestion] =
    names.map(n =>
      if (flag) buildRawSuggestionForExternalFragment(prefix, n)
      else RawSuggestion(prevFromPrefix(prefix).concat(n), isAKey = false)
    )

  protected def prevFromPrefix(prefix: String): String = {
    if (prefix.endsWith("/")) prefix
    else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
    else prefix + "/"
  }

  private def buildRawSuggestionForExternalFragment(prefix: String, n: String): RawSuggestion =
    RawSuggestion(
      value = prefix.concat(fixValue(n)),
      isAKey = false,
      category = "unknown",
      mandatory = false,
      displayText = Option(fixValue(n)),
      Seq.empty
    )

  private def fixValue(prefix: String): String =
    if (prefix.startsWith("/")) prefix.substring(1)
    else prefix
}

object PathSuggestor {
  def apply(
      fullUrl: String,
      prefix: String,
      alsConfiguration: ALSConfigurationState,
      targetClass: Option[String],
      hackForComponent: Boolean = false
  ): Future[PathSuggestor] = {
    var (fileUri, navPath) =
      fullUrl.split("#").toList match {
        case head :: tail => (head, tail.headOption.getOrElse(""))
        case _            => ("", "")
      }

    var newFileUri = fileUri
    if (hackForComponent && navPath.isEmpty) {
      newFileUri = fullUrl.concat(prefix)
      navPath = "/"
    }

    for {
      cached <-
        cacheFile(alsConfiguration, newFileUri)
    } yield {
      cached match {
        case Some(schema: DeclaresModel) => DeclarablePathSuggestor(schema, prefix, targetClass, hackForComponent)
        case None                        => PathNavigation(fileUri, navPath, prefix, alsConfiguration, hackForComponent)
      }
    }
  }

  private def cacheFile(alsConfig: ALSConfigurationState, fileUri: String): Future[Option[BaseUnit]] = {
    try {
      alsConfig.cache
        .fetch(fileUri)
        .map(f => Some(f.content))
        .recoverWith { case _ =>
          Future.successful(None)
        }
    } catch {
      case _: Exception =>
        Future.successful(None)
    }
  }
}
