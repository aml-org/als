package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.client.scala.model.domain.Server
import amf.apicontract.client.scala.model.domain.security.{ParametrizedSecurityScheme, SecurityRequirement}
import amf.apicontract.internal.metamodel.domain.ServerModel
import amf.apicontract.internal.metamodel.domain.security.{SecurityRequirementModel, SecuritySchemeModel}
import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.{ArrayRange, ObjectRange, RawSuggestion, SuggestionStructure}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request)) {
        val original = getSecurityNames(request)
        if (request.astPartBranch.isKeyLike || compatibleParametrizedSecurityScheme(request))
          original.map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
        else if (!request.astPartBranch.isKeyLike)
          original
            .map(r => r.copy(options = r.options.copy(rangeKind = ArrayRange, isKey = false)))
        else original
      } else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: ParametrizedSecurityScheme =>
        compatibleParametrizedSecurityScheme(request)
      case _: SecurityRequirement =>
        (request.fieldEntry.map(_.field).contains(SecurityRequirementModel.Name) || request.fieldEntry.isEmpty) &&
        underSecurityKey(request) &&
        (request.astPartBranch.isInArray || request.astPartBranch.isValue)
      case _: Server => request.fieldEntry.map(_.field).contains(ServerModel.Security)
      case _         => false
    }

  private def compatibleParametrizedSecurityScheme(request: AmlCompletionRequest) =
    request.astPartBranch.isInArray && underSecurityKey(request) && request.fieldEntry.isEmpty && request.amfObject
      .isInstanceOf[ParametrizedSecurityScheme]

  private def getSecurityNames(request: AmlCompletionRequest): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(
          Seq(SecuritySchemeModel.`type`.head.iri()), request.prefix, request.declarationProvider, None
    ).resolve(rawSuggestionBuilder(request))

  private def underSecurityKey(request: AmlCompletionRequest) =
    request.astPartBranch.parentEntryIs("security") ||
      request.astPartBranch.parentEntryIs("securedBy") // use metadata (dialect) here

  private def rawSuggestionBuilder(request: AmlCompletionRequest)(name: String, de: DomainElement): RawSuggestion = {
    // todo: aca revisar el DE para la logica de que si tiene scopes es array y sino scalar
    val options =
      if (request.astPartBranch.isKeyLike || compatibleParametrizedSecurityScheme(request))
        SuggestionStructure(isKey = true, rangeKind = ObjectRange)
      else if (!request.astPartBranch.isKeyLike)
        SuggestionStructure(rangeKind = ArrayRange)
      else SuggestionStructure()

    RawSuggestion.apply(name, options)
  }
}
