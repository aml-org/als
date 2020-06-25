package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.AmfObject
import amf.plugins.domain.webapi.metamodel.security.OAuth2FlowModel
import amf.plugins.domain.webapi.models.security.{OAuth2Settings, ParametrizedSecurityScheme, Scope}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object OaslikeSecurityScopesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecurityScopes"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      request.amfObject match {
        case _: Scope =>
          val scopes = getCurrentScopes(request.branchStack)
          getParametrizedScopes(request.branchStack)
            .filter(s => !scopes.contains(s))
            .map(t =>
              RawSuggestion(
                t,
                isAKey = false,
                category = CategoryRegistry(OAuth2FlowModel.`type`.head.iri(),
                                            OAuth2FlowModel.Scopes.value.iri(),
                                            request.actualDialect.id),
                mandatory = false
            ))
        case _ => Nil
      }
    }

  private def getCurrentScopes(branchStack: Seq[AmfObject]): Seq[String] = {
    branchStack.collectFirst({ case settings: OAuth2Settings => settings }) match {
      case Some(settings: OAuth2Settings) =>
        if (settings.flows.nonEmpty) settings.flows.head.scopes.flatMap(_.name.option()) else List()
      case _ => List()
    }
  }

  private def getParametrizedScopes(branchStack: Seq[AmfObject]): Seq[String] = {
    branchStack.collectFirst({ case p: ParametrizedSecurityScheme => p }) match {
      case Some(p: ParametrizedSecurityScheme) =>
        p.scheme.settings match {
          case s2: OAuth2Settings if (s2.flows.nonEmpty) => s2.flows.head.scopes.flatMap(_.name.option())
          case _                                         => Nil
        }
      case _ => Nil
    }
  }
}
