package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PathSuggestor {
  def suggest(): Future[Seq[RawSuggestion]]

  protected def buildSuggestions(names: Seq[String], prefix: String): Seq[RawSuggestion] =
    names.map(n => RawSuggestion(tunePrefixIfNeeded(prefix, n), n, isAKey = false, "unknown", mandatory = false))

  private def tunePrefixIfNeeded(prefix: String, n: String): String = {
    if (n.startsWith("#") || n.startsWith("/")) s"${normalizePrefix(prefix)}$n"
    else s"${prevFromPrefix(prefix)}$n"
  }
  protected def prevFromPrefix(prefix: String): String =
    if (prefix.endsWith("/")) prefix
    else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
    else prefix + "/"

  private def normalizePrefix(prefix: String): String = {
    if (prefix.contains("#/")) prefix.substring(0, prefix.lastIndexOf("#/"))
    else prefix
  }
}

object PathSuggestor {
  def apply(
      fileUri: String,
      prefix: String,
      alsConfiguration: ALSConfigurationState,
      targetClass: Option[String],
      bu: Option[BaseUnit],
      navPath: String
  ): Future[PathSuggestor] = {
    bu match {
      case Some(schema: DeclaresModel) => Future(DeclarablePathSuggestor(schema, prefix, targetClass))
      case _                           => Future(PathNavigation(fileUri, navPath, prefix, alsConfiguration))
    }
  }
}
