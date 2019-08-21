package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.SynthesizedField
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.{EndPoint, Parameter, WebApi}
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
          webApi.servers.flatMap(s => {
            val url = s.url.option().getOrElse("")
            s.variables.flatMap(_.name.option()).filter(n => url.contains(s"{$n}"))
          })
        case p: Parameter
            if p.binding.option().contains("path") && request.fieldEntry.exists(_.field == ParameterModel.Name) =>
          request.branchStack.headOption match {
            case Some(e: EndPoint) => endpointParams(e)
            case _                 => Nil
          }
        case _ => Nil
      }
      params.map(p => RawSuggestion(p, request.indentation, isAKey = true, "parameters"))
    }
  }

  private def endpointParams(e: EndPoint): Seq[String] = {
    e.parameters
      .filter(p => p.binding.option().contains("path") && p.annotations.contains(classOf[SynthesizedField]))
      .flatMap(_.name.option())
      .filter(n => e.path.option().getOrElse("").contains(s"{$n}"))
  }
}
