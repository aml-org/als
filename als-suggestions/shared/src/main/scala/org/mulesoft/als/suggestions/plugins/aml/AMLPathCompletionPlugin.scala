package org.mulesoft.als.suggestions.plugins.aml

import amf.apicontract.client.scala.model.document.ComponentModule
import amf.core.client.scala.model.document.ExternalFragment
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.common.{DirectoryResolver, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.pathnavigation.PathSuggestor
import org.mulesoft.amfintegration.AmfImplicits.NodeMappingImplicit
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.amfintegration.dialect.DialectKnowledge

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
        params.currentNode.flatMap(_.getTargetClass()),
        params.baseUnit
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
      baseUnit: BaseUnit,
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
      else if (isAExternalFragment(fullURI.concat(prefix), baseUnit))
        resolveInclusion(
          actualLocation,
          directoryResolver,
          prefix.concat("#/"),
          rootLocation,
          alsConfiguration,
          targetClass,
          baseUnit,
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

  private def isAExternalFragment(uri: String, baseUnit: BaseUnit): Boolean =
    baseUnit.references.find(_.location().contains(uri)) match {
      case Some(_: ExternalFragment) => true
      case Some(_: ComponentModule)  => true
      case _                         => false
    }
}
