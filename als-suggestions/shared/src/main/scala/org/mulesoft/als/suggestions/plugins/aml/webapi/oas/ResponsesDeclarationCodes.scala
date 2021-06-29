package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.api.WebApi
import amf.apicontract.internal.metamodel.domain.ResponseModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLKnownValueCompletions

import scala.concurrent.Future

object ResponsesDeclarationCodes extends AMLCompletionPlugin {
  override def id: String = "ResponsesDeclarationCodes"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (request.amfObject.isInstanceOf[WebApi] && request.yPartBranch.isKeyDescendantOf("responses"))
      new AMLKnownValueCompletions(
        ResponseModel.StatusCode,
        ResponseModel.`type`.head.iri(),
        request.actualDialect,
        request.yPartBranch.isKey,
        request.yPartBranch.isInArray || request.yPartBranch.isArray,
        true
      ).resolve()
    else emptySuggestion
  }
}
