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
      if (isWritingKEYMediaType(request)) {
        PatchedSuggestionsForDialect
          .getKnownValues(request.actualDialect.id, PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri())
          .map(
            p =>
              RawSuggestion
                .forObject(p.text,
                           CategoryRegistry(PayloadModel.`type`.head.iri(),
                                            PayloadModel.MediaType.value.name,
                                            request.actualDialect.id)))
      } else Nil
    }
  }
}
