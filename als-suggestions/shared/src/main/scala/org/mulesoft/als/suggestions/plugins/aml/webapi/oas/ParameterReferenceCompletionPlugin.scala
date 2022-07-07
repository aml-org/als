package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.{ParameterModel, PayloadModel}
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLJsonSchemaStyleDeclarationReferences

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ParameterReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (AMLJsonSchemaStyleDeclarationReferences.applies(request)) {
        request.amfObject match {
          case _: Parameter =>
            new AMLJsonSchemaStyleDeclarationReferences(
              request.actualDialect,
              paramIriMaps.keys.toSeq,
              request.amfObject.elementIdentifier(),
              request.astPartBranch,
              paramIriMaps
            )
              .resolve(request.declarationProvider)
          case _ => AMLJsonSchemaStyleDeclarationReferences.suggest(request)
        }
      } else Nil
    }
  }

  private val paramIriMaps =
    Map(ParameterModel.`type`.head.iri() -> "parameters", PayloadModel.`type`.head.iri() -> "parameters")
}
