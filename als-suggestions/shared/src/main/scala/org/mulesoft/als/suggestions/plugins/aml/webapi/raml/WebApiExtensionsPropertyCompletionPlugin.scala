package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.api.WebApi
import amf.core.client.scala.model.document.{BaseUnit, ExtensionLike}
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLPathCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WebApiExtensionsPropertyCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "WebApiExtensionsPropertyCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.baseUnit match {
      case e: ExtensionLike[_]
          if params.amfObject
            .isInstanceOf[WebApi] && params.fieldEntry.isEmpty =>
        suggestOverExtends(e,
                           params.yPartBranch.isKey,
                           params.directoryResolver,
                           params.prefix,
                           params.rootUri,
                           params.alsConfigurationState)
      case _ => emptySuggestion
    }
  }

  private def suggestOverExtends(e: ExtensionLike[_],
                                 isKey: Boolean,
                                 directoryResolver: DirectoryResolver,
                                 prefix: String,
                                 rootLocation: Option[String],
                                 alsConfiguration: ALSConfigurationState): Future[Seq[RawSuggestion]] = {
    if (isKey) Future { Seq(RawSuggestion.forKey("extends", mandatory = true)) } else
      AMLPathCompletionPlugin.resolveInclusion(e.location().getOrElse(""),
                                               directoryResolver,
                                               prefix,
                                               rootLocation,
                                               alsConfiguration)
  }
}
