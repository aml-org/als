package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.ResponseModel
import amf.plugins.domain.webapi.models.Response
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLKnownValueCompletions

import scala.concurrent.Future

object ExampleMediaType extends AMLCompletionPlugin {
  override def id: String = "ExampleMediaType"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.fieldEntry match {
      case Some(fe)
          if fe.field == ResponseModel.Examples && request.yPartBranch.isKey && request.amfObject
            .isInstanceOf[Response] =>
        new AMLKnownValueCompletions(
          ExampleModel.MediaType,
          ExampleModel.`type`.head.iri(),
          request.actualDialect,
          request.yPartBranch.isKey,
          request.indentation,
          request.yPartBranch.isInArray || request.yPartBranch.isArray
        ).resolve()
      case _ => emptySuggestion
    }
  }
}
