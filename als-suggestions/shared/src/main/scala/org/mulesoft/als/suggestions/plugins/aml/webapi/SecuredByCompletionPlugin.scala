package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.client.scala.model.domain.Server
import amf.apicontract.client.scala.model.domain.security._
import amf.apicontract.internal.metamodel.domain.ServerModel
import amf.apicontract.internal.metamodel.domain.security.{SecurityRequirementModel, SecuritySchemeModel}
import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request))
        getSecurityNames(request)
      else Nil
    }
  }

  protected def isSecurityScalarValue(request: AmlCompletionRequest): Boolean =
    !isAKey(request) && request.astPartBranch.parentEntryIs("security")

  protected def isAKey(request: AmlCompletionRequest): Boolean =
    request.astPartBranch.isKeyLike

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean =
    !isSecurityScalarValue(request) &&
      (request.amfObject match {
        case _: ParametrizedSecurityScheme =>
          compatibleParametrizedSecurityScheme(request)
        case _: SecurityRequirement =>
          (request.fieldEntry.map(_.field).contains(SecurityRequirementModel.Name) || request.fieldEntry.isEmpty) &&
          underSecurityKey(request) &&
          (request.astPartBranch.isInArray || request.astPartBranch.isValue)
        case _: Server =>
          request.fieldEntry.map(_.field).contains(ServerModel.Security)
        case _         => false
      })

  protected def compatibleParametrizedSecurityScheme(request: AmlCompletionRequest) =
    request.astPartBranch.isInArray && underSecurityKey(request) && request.fieldEntry.isEmpty && request.amfObject
      .isInstanceOf[ParametrizedSecurityScheme]

  private def getSecurityNames(request: AmlCompletionRequest): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(
          Seq(SecuritySchemeModel.`type`.head.iri()), request.prefix, request.declarationProvider, None
    ).resolve(rawSuggestionBuilder(request))

  private def underSecurityKey(request: AmlCompletionRequest) =
    request.astPartBranch.parentEntryIs("security") ||
      request.astPartBranch.parentEntryIs("securedBy") // use metadata (dialect) here

  def isRAMLspec(de: DomainElement): Boolean = {
    de.typeIris.nonEmpty
  }

  protected def rawSuggestionBuilder(request: AmlCompletionRequest)(name: String, de: DomainElement): RawSuggestion

  protected def hasScopes(de: DomainElement): Boolean = {
    de match {
      case s: SecurityScheme =>
        s.settings match {
          case o2: OAuth2Settings =>
            o2.flows.exists(_.scopes.nonEmpty)
          case oi: OpenIdConnectSettings => oi.scopes.nonEmpty
          case _ => false
        }
      case _ => false
    }
  }
}
