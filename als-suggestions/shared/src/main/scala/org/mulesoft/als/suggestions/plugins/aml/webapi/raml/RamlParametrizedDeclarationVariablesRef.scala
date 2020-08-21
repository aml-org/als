package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.AmfObject
import amf.core.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration, VariableValue}
import amf.plugins.domain.webapi.models.templates.{ParametrizedResourceType, ParametrizedTrait}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object RamlParametrizedDeclarationVariablesRef extends AMLCompletionPlugin {
  override def id: String = "RamlParametrizedDeclarationVariables"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      params.amfObject match {
        case p: ParametrizedDeclaration if params.yPartBranch.isKey || params.yPartBranch.isArray =>
          p.target.linkTarget
            .collectFirst({ case p: AbstractDeclaration => p })
            .getOrElse(p.target)
            .variables
            .flatMap(_.option())
            .map(RawSuggestion.forKey(_, "parameters", mandatory = true))
        case _ => Nil
      }
    )
  }
}
