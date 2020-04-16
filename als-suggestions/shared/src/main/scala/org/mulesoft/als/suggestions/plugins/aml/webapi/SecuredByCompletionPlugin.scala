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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request) && (!request.yPartBranch.isJson || (request.yPartBranch.isJson && request.yPartBranch.isInArray))) {
        val original = getSecurityNames(request.prefix, request.declarationProvider)
        val forKey =
          if (request.yPartBranch.isKey)
            original.map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
          else original

        if (request.fieldEntry.exists(_.field.`type`.isInstanceOf[ArrayLike]) && !request.yPartBranch.isInArray)
          forKey.map(r => r.copy(options = r.options.copy(rangeKind = ArrayRange)))
        else forKey

      } else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case p: ParametrizedSecurityScheme =>
        p.name.value() == "k" ||
          (p.name.value() != "k" && !request.yPartBranch.parentEntryIs(p.name.value())) && !request.yPartBranch
            .isKeyDescendantOf(p.name.value())
      case w: WebApi =>
        request.fieldEntry.exists(t => t.field == WebApiModel.Security) && (request.yPartBranch.isInArray || request.yPartBranch.isValue)
      case w: Operation =>
        request.fieldEntry.exists(t => t.field == OperationModel.Security) && request.yPartBranch.isInArray
      case s: Server => request.fieldEntry.exists(t => t.field == ServerModel.Security)
      case _         => false
    }
  }

  private def getSecurityNames(prefix: String, dp: DeclarationProvider): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()
}
