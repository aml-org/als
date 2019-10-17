package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.{FieldEntry, Value}
import amf.plugins.domain.webapi.metamodel.security.OAuth2SettingsModel
import amf.plugins.domain.webapi.models.security.{OAuth2Settings, ParametrizedSecurityScheme, Scope}
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
      request.fieldEntry match {
        case Some(FieldEntry(OAuth2SettingsModel.Scopes, Value(array: AmfArray, _))) =>
          val scopes = array.values.collect({ case s: Scope => s.name.option() }).flatten
          getParametrizedScopes(request.branchStack)
            .filter(s => !scopes.contains(s))
            .map(
              t =>
                RawSuggestion(t,
                              isAKey = false,
                              category = CategoryRegistry(OAuth2SettingsModel.`type`.head.iri(),
                                                          OAuth2SettingsModel.Scopes.value.iri())))
        case _ => Nil

      }
    }

  private def getParametrizedScopes(branchStack: Seq[AmfObject]): Seq[String] = {
    branchStack.headOption match {
      case Some(p: ParametrizedSecurityScheme) =>
        p.scheme.settings match {
          case s2: OAuth2Settings => s2.scopes.flatMap(_.name.option())
          case _                  => Nil
        }
      case _ => Nil
    }
  }
}
