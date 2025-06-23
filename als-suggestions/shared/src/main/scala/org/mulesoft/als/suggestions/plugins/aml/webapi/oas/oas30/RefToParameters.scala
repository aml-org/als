package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.client.scala.model.domain.{DomainElement, NamedDomainElement}
import amf.core.internal.annotations.DeclaredHeader
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLJsonSchemaStyleDeclarationReferences
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RefToParameters extends AMLCompletionPlugin {
  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (AMLJsonSchemaStyleDeclarationReferences.applies(request)) {
      request.amfObject match {
        case p: Parameter => Future { resolveParam(request, p) }
        case _            => AMLJsonSchemaStyleDeclarationReferences.resolve(request)
      }
    } else emptySuggestion
  }

  private def resolveParam(request: AmlCompletionRequest, p: Parameter) = {
    if (Oas30ParameterStructure.synthesizedHeader(p)) headerSuggestions(request)
    else parametersSuggestions(request)
  }

  private def parametersSuggestions(request: AmlCompletionRequest) = {
    composeSuggestion(request, "parameters", paramsFilter)
  }

  private def headerSuggestions(request: AmlCompletionRequest) = {
    composeSuggestion(request, "headers", headersFilter)
  }

  private val headersFilter = (p: DomainElement) => p.annotations.contains(classOf[DeclaredHeader])
  private val paramsFilter  = (p: DomainElement) => !headersFilter(p)

  private def composeSuggestion(
      request: AmlCompletionRequest,
      path: String,
      fn: DomainElement => Boolean
  ): Seq[RawSuggestion] = {
    val dcl     = declarationPath(request.actualDocumentDefinition)
    val strings = declarations(request, fn).map(d => s"#/$dcl$path/$d")

    AMLJsonSchemaStyleDeclarationReferences
      .resolveRoutes(strings, request.astPartBranch)
  }

  private def declarationPath(dialect: DocumentDefinition) = {
    dialect.documents().declarationsPath().option().map(_ + "/").getOrElse("")
  }

  private def declarations(request: AmlCompletionRequest, filterFn: DomainElement => Boolean): Seq[String] = {
    request.declarationProvider
      .filterLocalByType(ParameterModel.`type`.head.iri())
      .filter(filterFn)
      .collect({ case n: NamedDomainElement => n.name.option() })
      .flatten
      .toSeq
  }
}
