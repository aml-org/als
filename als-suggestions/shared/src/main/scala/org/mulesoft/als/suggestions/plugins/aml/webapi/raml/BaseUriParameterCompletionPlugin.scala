package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.api.WebApi
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.UrlTemplateParam

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BaseUriParameterCompletionPlugin extends UrlTemplateParam {

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case webApi: WebApi if request.astPartBranch.isKeyDescendantOf("baseUriParameters") =>
        resolveWebApi(webApi)
      case _ => super.resolve(request)
    }
  }

  private def resolveWebApi(webApi: WebApi) = Future {
    webApi.servers.flatMap(serverParams).map(toRaw)
  }
}
