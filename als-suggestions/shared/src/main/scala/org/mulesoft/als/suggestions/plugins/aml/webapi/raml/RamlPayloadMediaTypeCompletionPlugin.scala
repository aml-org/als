package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.metamodel.domain.ShapeModel
import amf.plugins.domain.webapi.metamodel.PayloadModel
import amf.plugins.domain.webapi.models.{Operation, Payload}
import org.mulesoft.als.common.YPartBranch
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
      if (isWrittingKEYMediaType(request)) {
        PatchedSuggestionsForDialect
          .getKnownValues(request.actualDialect.id, PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri())
          .map(p => RawSuggestion(p.text, request.indentation, isAKey = true))
      } else Nil
    }
  }

  private def isWrittingKEYMediaType(request: AmlCompletionRequest) = {
    request.yPartBranch.isKey &&
    (request.branchStack.headOption match {
      case Some(p: Payload) =>
        p.schema.fields.filter(f => f._1 != ShapeModel.Name).fields().isEmpty && (p.mediaType
          .option()
          .isEmpty || inMediaType(request.yPartBranch))
      case Some(o: Operation) => request.yPartBranch.isKey && request.yPartBranch.isKeyDescendanceOf("body")
      case _                  => false
    })
  }

//  private def isWrittingValueMediaType(request:AmlCompletionRequest) = {
//    request.yPartBranch.isValue && request.yPartBranch.is
//  }
  // todo : replace hack when amf keep lexical information over media type field in payload
  private def inMediaType(yPartBranch: YPartBranch): Boolean =
    yPartBranch.stringValue.contains('/') && yPartBranch.isKeyDescendanceOf("body")
}
