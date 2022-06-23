package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.internal.metamodel.domain.{EndPointModel, OperationModel}
import amf.core.client.scala.model.document.Fragment
import amf.core.client.scala.model.domain.templates.AbstractDeclaration
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLRefTagCompletionPlugin, AMLRootDeclarationsCompletionPlugin}
import org.mulesoft.amfintegration.AbstractDeclarationInformation
import org.mulesoft.amfintegration.AbstractDeclarationInformation.ElementInfo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlAbstractDefinition extends AMLCompletionPlugin {
  override def id: String = "RamlAbstractDefinition"

  private val ignoredPlugins: Set[AMLCompletionPlugin] =
    Set(AMLRefTagCompletionPlugin, AMLRootDeclarationsCompletionPlugin, AMLLibraryPathCompletion)

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val info = if (params.yPartBranch.isIncludeTagValue) None else elementInfo(params)

    info
      .map { info =>
        if (params.baseUnit.isInstanceOf[Fragment])
          info.element.fields.filter(t => !(t._1 == EndPointModel.Path || t._1 == OperationModel.Method))
        val newRequest =
          AmlCompletionRequestBuilder.forElement(
            info.element,
            info.original,
            params.declarationProvider.filterLocal(info.name, info.iri),
            params,
            ignoredPlugins
          )
        newRequest.completionsPluginHandler
          .pluginSuggestions(newRequest)
          .map(seq => {
            if (
              params.branchStack.headOption.exists(_.isInstanceOf[AbstractDeclaration]) && !params.baseUnit
                .isInstanceOf[Fragment] && params.yPartBranch.isKey
            )
              seq ++ Seq(RawSuggestion.forKey("usage", "docs", mandatory = false))
            else seq
          })
      }
      .getOrElse(Future.successful(Nil))
  }

  private def findAbstractDeclaration(params: AmlCompletionRequest) = {
    params.amfObject match {
      case a: AbstractDeclaration => Some(a)
      case _                      => params.branchStack.collectFirst({ case a: AbstractDeclaration => a })
    }
  }

  private def elementInfo(params: AmlCompletionRequest): Option[ElementInfo] =
    findAbstractDeclaration(params).flatMap(
      AbstractDeclarationInformation
        .extractInformation(_, params.baseUnit, params.alsConfigurationState.getAmfConfig)
    )

}
