package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.plugins.domain.webapi.metamodel.WebApiModel
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.models.WebApi
import amf.plugins.domain.webapi.models.security.ParametrizedSecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWrittingSecuredBy(request)) {
        val original = getSecurityNames(request.prefix, request.declarationProvider)
        if (request.yPartBranch.isKey) original.map(r => r.copy(isKey = true, whiteSpacesEnding = request.indentation))
        else original
      } else Nil
    }
  }

  private def isWrittingSecuredBy(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case p: ParametrizedSecurityScheme =>
        p.name.value() == "k" || (p.name.value() != "k" && !request.yPartBranch.parentEntryIs(p.name.value()))
      case w: WebApi => request.fieldEntry.exists(t => t.field == WebApiModel.Security)
      case _         => false
    }
  }

  private def getSecurityNames(prefix: String, dp: DeclarationProvider) =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()
}
