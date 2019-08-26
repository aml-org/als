package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.plugins.domain.webapi.metamodel.{OperationModel, WebApiModel}
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.models.{Operation, WebApi}
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
      if (isWrittingSecuredBy(request) && (!request.yPartBranch.isJson || (request.yPartBranch.isJson && request.yPartBranch.isInArray))) {
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
      case w: WebApi    => request.fieldEntry.exists(t => t.field == WebApiModel.Security)
      case w: Operation => request.fieldEntry.exists(t => t.field == OperationModel.Security)
      case _            => false
    }
  }

  private def getSecurityNames(prefix: String, dp: DeclarationProvider) =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()
}
