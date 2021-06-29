package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.client.scala.model.domain.Server
import amf.apicontract.client.scala.model.domain.security.{ParametrizedSecurityScheme, SecurityRequirement}
import amf.apicontract.internal.metamodel.domain.ServerModel
import amf.apicontract.internal.metamodel.domain.security.{SecurityRequirementModel, SecuritySchemeModel}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.NonPatchHacks
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.plugins.aml.patched.JsonExceptions
import org.mulesoft.als.suggestions.{ArrayRange, ObjectRange, RawSuggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuredByCompletionPlugin extends AMLCompletionPlugin with NonPatchHacks {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request)) {
        val original = getSecurityNames(request.prefix, request.declarationProvider)
        val forKey =
          if (notValue(request.yPartBranch) || compatibleParametrizedSecurityScheme(request))
            original.map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
          else original

        if (!request.yPartBranch.isArray && !request.yPartBranch.isInArray)
          forKey.map(r => r.copy(options = r.options.copy(rangeKind = ArrayRange, isKey = false)))
        else forKey

      } else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: ParametrizedSecurityScheme =>
        compatibleParametrizedSecurityScheme(request)
      case _: SecurityRequirement =>
        (request.fieldEntry.exists(_.field == SecurityRequirementModel.Name) || request.fieldEntry.isEmpty) && underSecurityKey(
          request) && (request.yPartBranch.isInArray || request.yPartBranch.isValue || JsonExceptions.SecuredBy
          .isJsonException(request.yPartBranch))
      case s: Server => request.fieldEntry.exists(t => t.field == ServerModel.Security)
      case _         => false
    }

  private def compatibleParametrizedSecurityScheme(request: AmlCompletionRequest) =
    request.yPartBranch.isInArray && underSecurityKey(request) && request.fieldEntry.isEmpty && request.amfObject
      .isInstanceOf[ParametrizedSecurityScheme]

  private def getSecurityNames(prefix: String, dp: DeclarationProvider): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()

  private def underSecurityKey(request: AmlCompletionRequest) = {
    request.yPartBranch.isDescendanceOf("security") || request.yPartBranch.isDescendanceOf("securedBy") // use metadata (dialect) here
  }
}
