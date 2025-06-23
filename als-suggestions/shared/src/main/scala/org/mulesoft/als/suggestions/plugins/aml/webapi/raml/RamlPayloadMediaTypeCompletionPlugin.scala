package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.internal.metamodel.domain.PayloadModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
import org.mulesoft.als.suggestions.plugins.aml.patched.PatchedSuggestionsForDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlPayloadMediaTypeCompletionPlugin extends AMLCompletionPlugin with PayloadMediaTypeSeeker {
  override def id: String = "RamlPayloadMediaTypeCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingKeyMediaType(request)) {
        PatchedSuggestionsForDialect
          .getKnownValues(request.actualDocumentDefinition.baseUnit.id, PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri())
          .map(p =>
            RawSuggestion
              .forObject(
                p.text,
                CategoryRegistry(
                  PayloadModel.`type`.head.iri(),
                  PayloadModel.MediaType.value.name,
                  request.actualDocumentDefinition.baseUnit.id
                )
              )
          )
      } else Nil
    }
  }
}
