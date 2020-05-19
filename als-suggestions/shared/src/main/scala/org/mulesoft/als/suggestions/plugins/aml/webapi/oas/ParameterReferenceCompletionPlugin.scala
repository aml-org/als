package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.domain.webapi.metamodel.{ParameterModel, PayloadModel}
import amf.plugins.domain.webapi.models.{EndPoint, Parameter}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLJsonSchemaStyleDeclarationReferences
import org.mulesoft.als.common.SemanticNamedElement._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ParameterReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (AMLJsonSchemaStyleDeclarationReferences.applies(request)) {
        request.amfObject match {
          case p: Parameter =>
            new AMLJsonSchemaStyleDeclarationReferences(request.actualDialect,
                                                        paramIriMaps.keys.toSeq,
                                                        request.amfObject.elementIdentifier(),
                                                        request.yPartBranch,
                                                        paramIriMaps)
              .resolve(request.declarationProvider)
          case _ => AMLJsonSchemaStyleDeclarationReferences.suggest(request)
        }
      } else Nil
    }
  }

  private val paramIriMaps =
    Map(ParameterModel.`type`.head.iri() -> "parameters", PayloadModel.`type`.head.iri() -> "parameters")
}
