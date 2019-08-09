package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.SynthesizedField
import amf.plugins.domain.webapi.models.{EndPoint, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object BaseUriParameterCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "BaseUriParameterCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful {
      val params = request.amfObject match {
        case webApi: WebApi if request.yPartBranch.isKeyDescendanceOf("baseUriParameters") =>
          webApi.servers.flatMap(_.variables.flatMap(_.name.option()))
        case e: EndPoint if request.yPartBranch.isKeyDescendanceOf("uriParameter") => endpointParams(e)
        case _                                                                     => Nil
      }
      params.map(RawSuggestion.forKey)
    }
  }

  private def endpointParams(e: EndPoint): Seq[String] = {
    val local: Seq[String] = e.parameters
      .filter(p => p.binding.option().contains("path") && p.annotations.contains(classOf[SynthesizedField]))
      .flatMap(_.name.option())
    e.parent.map(endpointParams).getOrElse(Nil) ++ local
  }
}
