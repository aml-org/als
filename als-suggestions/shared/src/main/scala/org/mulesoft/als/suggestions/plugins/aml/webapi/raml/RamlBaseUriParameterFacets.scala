package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlBaseUriParameterFacets extends AMLCompletionPlugin {
  override def id: String = "RamlBaseUriParameterFacets"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case w: WebApi =>
          w.servers
            .flatMap(_.variables.find(_.position().exists(l => l.contains(request.position))))
            .headOption match {
            case Some(p) if !isWrittingParamName(request.yPartBranch) =>
              RamlParamsCompletionPlugin.computeParam(p, Nil, getIndentation(request.baseUnit, request.position))
            case _ => Nil
          }
        case _ => Nil
      }
    }
  }

  private def isWrittingParamName(yPart: YPartBranch) = {
    yPart
      .ancestorOf(classOf[YMapEntry])
      .exists(p => p.key.asScalar.exists(s => Seq("baseUriParameters", "uriParameters").contains(s.text)))
  }
}
