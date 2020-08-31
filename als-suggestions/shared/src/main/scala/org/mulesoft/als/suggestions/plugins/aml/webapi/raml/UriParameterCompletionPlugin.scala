package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.UrlTemplateParam

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UriParameterCompletionPlugin extends UrlTemplateParam {
  override def id: String = "UriParameterCompletionPlugin"
  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case endPoint: EndPoint if request.yPartBranch.isKeyDescendantOf("uriParameters") =>
          endpointParams(endPoint).map(toRaw)
        case _ => Nil
      }
    }
  }

}
