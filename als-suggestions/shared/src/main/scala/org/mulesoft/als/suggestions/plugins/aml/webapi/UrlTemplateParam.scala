package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.client.scala.model.domain.{EndPoint, Parameter, Server}
import amf.apicontract.internal.metamodel.domain.ParameterModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

import scala.concurrent.Future

trait UrlTemplateParam extends AMLCompletionPlugin {
  override def id: String = "UrlTemplateParam"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful {
      val params = request.amfObject match {
        case p: Parameter
            if (p.binding.option().contains("path") && isName(request)) || request.yPartBranch.isKeyDescendantOf(
              "variables") =>
          request.branchStack.headOption match {
            case Some(e: EndPoint) => endpointParams(e)
            case Some(s: Server)   => serverParams(s)
            case _                 => Nil
          }
        case endPoint: EndPoint if request.yPartBranch.isKeyDescendantOf("uriParameters") =>
          endpointParams(endPoint)
        case _ => Nil
      }
      params.map(toRaw)
    }
  }

  private def isName(request: AmlCompletionRequest) = request.fieldEntry.exists(_.field == ParameterModel.Name)

  protected def endpointParams(e: EndPoint): Seq[String] = {
    e.parameters
      .filter(p => p.binding.option().contains("path") && p.annotations.isVirtual)
      .flatMap(_.name.option())
      .filter(n => e.path.option().getOrElse("").contains(s"{$n}"))
  }

  protected def toRaw(s: String): RawSuggestion = RawSuggestion.forObject(s, "parameters")

  protected def serverParams(server: Server): Seq[String] = {
    val url = server.url.option().getOrElse("")
    server.variables
      .flatMap(_.name.option())
      .filter(n => url.contains(s"{$n}"))
  }
}
