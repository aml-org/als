package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.common.{DirectoryResolver, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.pathnavigation.PathSuggestor
import org.mulesoft.amfintegration.AmfImplicits.NodeMappingImplicit
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.amfintegration.dialect.DialectKnowledge
import org.mulesoft.amfintegration.dialect.extensions.ApiExtensions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLPathCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLPathCompletionPlugin"

  // exclude file name
  private def extractPath(parts: String): String =
    if (parts.lastIndexOf('/') >= 0)
      parts.substring(0, parts.lastIndexOf('/') + 1)
    else ""

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (ramlOrJsonInclusion(params)) {
      resolveInclusion(
        params.baseUnit.location().getOrElse(""),
        params.directoryResolver,
        params.prefix,
        params.rootUri,
        params.alsConfigurationState,
        params.currentNode.flatMap(_.getTargetClass())
      )
    } else emptySuggestion

  private def ramlOrJsonInclusion(params: AmlCompletionRequest) = {
    params.astPartBranch match {
      case yPartBranch: YPartBranch =>
        DialectKnowledge.isRamlInclusion(yPartBranch, params.nodeDialect) || DialectKnowledge.isJsonInclusion(
          yPartBranch,
          params.nodeDialect
        )
      case _ => false
    }
  }
  def resolveInclusion(
      actualLocation: String,
      directoryResolver: DirectoryResolver,
      prefix: String,
      rootLocation: Option[String],
      alsConfiguration: ALSConfigurationState,
      targetClass: Option[String],
      flagExternalFragment: Boolean = false
  ): Future[Seq[RawSuggestion]] = {
    val baseLocation: String =
      if (prefix.startsWith("/")) rootLocation.getOrElse(actualLocation)
      else actualLocation
    val fullPath = baseLocation.toPath(alsConfiguration.platform)
    val baseDir  = extractPath(fullPath) // root path for file

    val relativePath = extractPath(prefix)
    val fullURI =
      s"${baseDir.stripSuffix("/")}/${relativePath.stripPrefix("/")}".toAmfUri(alsConfiguration.platform)

    if (referenceToItself(prefix))
      emptySuggestion
    else {
      if (referenceToAnExternalFragment(fullURI))
        PathSuggestor(fullURI, prefix, alsConfiguration, targetClass, flagExternalFragment).flatMap(_.suggest())
      else if (isAExternalFragment(prefix))
        resolveInclusion(
          actualLocation,
          directoryResolver,
          prefix.concat("#/"),
          rootLocation,
          alsConfiguration,
          targetClass,
          true
        )
      else
        FilesEnumeration(
          directoryResolver,
          alsConfiguration,
          actualLocation.toPath(alsConfiguration.platform),
          relativePath
        )
          .filesIn(fullURI)
    }

  }

  private def referenceToItself(prefix: String): Boolean = prefix.startsWith("#")
  private def referenceToAnExternalFragment(fullUri: String): Boolean =
    fullUri.contains("#") && !fullUri.startsWith("#")

  private def isAExternalFragment(prefix: String): Boolean =
    !ApiExtensions.getValidExtensions.filter(prefix.toLowerCase().endsWith(_)).isEmpty
}
