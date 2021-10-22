package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.security.{
  OAuth2Flow,
  OAuth2Settings,
  ParametrizedSecurityScheme,
  Scope
}
import amf.apicontract.internal.metamodel.domain.security.OAuth2FlowModel
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecurityScopesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecurityScopes"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      request.amfObject match {
        case _: Scope =>
          val scopes = request.branchStack.headOption
            .collect({ case f: OAuth2Flow => f.scopes.flatMap(_.name.option()) })
            .getOrElse(Nil)
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
        case p: ParametrizedSecurityScheme
            if request.fieldEntry.isEmpty && request.yPartBranch.isKey && p.scheme.`type`.value() == "OAuth 2.0" =>
          Seq(RawSuggestion.arrayProp("scopes", "security"))

        case _ => Nil
      }
    }

  private def getParametrizedScopes(branchStack: Seq[AmfObject]): Seq[String] = {
    branchStack.collectFirst({ case p: ParametrizedSecurityScheme => p }) match {
      case Some(p: ParametrizedSecurityScheme) =>
        p.scheme.settings match {
          case s2: OAuth2Settings => s2.flows.head.scopes.flatMap(_.name.option())
          case _                  => Nil
        }
      case _ => Nil
    }
  }
}
