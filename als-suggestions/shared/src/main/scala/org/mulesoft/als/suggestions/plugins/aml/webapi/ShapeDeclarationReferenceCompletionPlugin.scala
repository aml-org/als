package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.client.scala.model.domain.Shape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShapeDeclarationReferenceCompletionPlugin extends AMLCompletionPlugin with DeclaredTypesSuggestions {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case s: Shape if params.astPartBranch.isValue =>
          val iri = extractIri(params, s)
          val declaredSuggestions =
            getDeclaredSuggestions(params, s.name.option(), iri)

          suggestAllTypes(params, declaredSuggestions)
        case _ => Nil
      }
    }
  }
}
