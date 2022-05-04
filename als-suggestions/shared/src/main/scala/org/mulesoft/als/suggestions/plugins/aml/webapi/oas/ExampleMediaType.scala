package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.Response
import amf.apicontract.internal.metamodel.domain.ResponseModel
import amf.shapes.internal.domain.metamodel.ExampleModel
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
          if fe.field == ResponseModel.Examples && request.astPartBranch.isKey && request.amfObject
            .isInstanceOf[Response] =>
        new AMLKnownValueCompletions(
          ExampleModel.MediaType,
          ExampleModel.`type`.head.iri(),
          request.actualDialect,
          request.astPartBranch.isKey,
          request.astPartBranch.isInArray || request.astPartBranch.isArray,
          true
        ).resolve()
      case _ => emptySuggestion
    }
  }
}
