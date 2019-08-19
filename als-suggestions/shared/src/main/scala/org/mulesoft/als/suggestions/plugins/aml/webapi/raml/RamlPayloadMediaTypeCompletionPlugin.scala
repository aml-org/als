package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.metamodel.domain.ShapeModel
import amf.plugins.domain.webapi.metamodel.PayloadModel
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.PatchedSuggestionsForDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlPayloadMediaTypeCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RamlPayloadMediaTypeCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.branchStack.headOption match {
        case Some(p: Payload)
            if request.yPartBranch.isKey
              && p.schema.fields.filter(f => f._1 != ShapeModel.Name).fields().isEmpty
              && p.mediaType.option().isEmpty =>
          PatchedSuggestionsForDialect
            .getKnownValues(request.actualDialect.id,
                            PayloadModel.`type`.head.iri(),
                            PayloadModel.MediaType.value.iri())
            .map(p => RawSuggestion(p.text, request.indentation, isAKey = true))
        case _ => Nil
      }

    }
  }
}
