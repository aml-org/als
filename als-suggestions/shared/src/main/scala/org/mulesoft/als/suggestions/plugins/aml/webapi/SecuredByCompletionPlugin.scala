package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.metamodel.Type.ArrayLike
import amf.core.model.domain.AmfObject
import amf.plugins.domain.webapi.metamodel.{OperationModel, ServerModel, WebApiModel}
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.models.security.{ParametrizedSecurityScheme, SecurityRequirement}
import amf.plugins.domain.webapi.models.{Operation, Server, WebApi}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{ArrayRange, ObjectRange, RawSuggestion}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.plugins.aml.patched.JsonExceptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request)) {
        val original = getSecurityNames(request.prefix, request.declarationProvider)
        val forKey =
          if (request.yPartBranch.isKey)
            original.map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
          else original

        if (!request.yPartBranch.isArray)
          forKey.map(r => r.copy(options = r.options.copy(rangeKind = ArrayRange, isKey = false)))
        else forKey

      } else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case _: SecurityRequirement =>
        request.fieldEntry.isEmpty && (request.yPartBranch.isDescendanceOf("security") || request.yPartBranch.isDescendanceOf(
          "securedBy")) && (request.yPartBranch.isArray || request.yPartBranch.isValue || JsonExceptions.SecuredBy
          .isJsonException(request.yPartBranch))
      case s: Server => request.fieldEntry.exists(t => t.field == ServerModel.Security)
      case _         => false
    }
  }

  private def getSecurityNames(prefix: String, dp: DeclarationProvider): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()
}
