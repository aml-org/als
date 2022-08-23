package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.security.SecurityRequirement
import amf.apicontract.internal.metamodel.domain.security.SecuritySchemeModel
import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.{ArrayRange, RawSuggestion, SuggestionStructure}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request)) getSecurityNames(request)
      else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case _: SecurityRequirement =>
        request.fieldEntry.exists(_.field == SecuritySchemeModel.Name) ||
        request.fieldEntry.isEmpty && request.astPartBranch.parentEntryIs("security")
      case _ => false
    }
  }

  private def getSecurityNames(request: AmlCompletionRequest): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(
          Seq(SecuritySchemeModel.`type`.head.iri()),
          request.prefix,
          request.declarationProvider, None
    ).resolve(rawSuggestionBuilder(request))

  private def rawSuggestionBuilder(request: AmlCompletionRequest)(name: String, de: DomainElement): RawSuggestion = {
    val options =
      if (request.astPartBranch.isKey || request.astPartBranch.isInArray)
        SuggestionStructure(isKey = true, rangeKind = ArrayRange)
      else SuggestionStructure(rangeKind = ArrayRange)

    RawSuggestion.apply(name, options)
  }
}
