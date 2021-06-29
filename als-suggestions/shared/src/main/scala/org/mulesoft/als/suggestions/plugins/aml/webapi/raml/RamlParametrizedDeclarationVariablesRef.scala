package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object RamlParametrizedDeclarationVariablesRef extends AMLCompletionPlugin {
  override def id: String = "RamlParametrizedDeclarationVariables"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      getParametrizedDeclaration(params) match {
        case Some(p) if p.target != null =>
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

  private def getParametrizedDeclaration(params: AmlCompletionRequest): Option[ParametrizedDeclaration] = {
    params.amfObject match {
      case declaration: ParametrizedDeclaration if params.yPartBranch.isKey || params.yPartBranch.isArray =>
        Some(declaration)
      case _ =>
        params.branchStack.headOption
          .collectFirst({ case p: ParametrizedDeclaration if params.yPartBranch.isKey => p })
    }
  }
}
