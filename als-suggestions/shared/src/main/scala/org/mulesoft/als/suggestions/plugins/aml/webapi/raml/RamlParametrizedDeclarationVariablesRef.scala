package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.AmfObject
import amf.core.model.domain.templates.VariableValue
import amf.plugins.domain.webapi.models.templates.{ParametrizedResourceType, ParametrizedTrait}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object RamlParametrizedDeclarationVariablesRef extends AMLCompletionPlugin {
  override def id: String = "RamlParametrizedDeclarationVariables"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if (params.amfObject.isInstanceOf[VariableValue] && params.yPartBranch.isKey) {
        getVariablesFromParent(params.branchStack.headOption)
          .map(RawSuggestion.forKey(_, "parameters", mandatory = false))
      } else Nil
    )

  }

  private def getVariablesFromParent(parent: Option[AmfObject]): Seq[String] =
    parent
      .collectFirst({ case p: ParametrizedResourceType => p })
      .orElse(parent.collectFirst({ case t: ParametrizedTrait => t }))
      .map(_.target.variables.flatMap(_.option()))
      .getOrElse(Nil)
}
