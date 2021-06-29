package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.api.WebApi
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10ParamsCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits.AlsLexicalInformation
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class RamlBaseUriParameterFacets(typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin)
    extends AMLCompletionPlugin {

  override def id: String = "RamlBaseUriParameterFacets"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case w: WebApi =>
          w.servers
            .flatMap(_.variables.find(_.position().exists(l => l.contains(request.position.toAmfPosition))))
            .headOption match {
            case Some(p) if !isWritingParamName(request.yPartBranch) =>
              Raml10ParamsCompletionPlugin.computeParam(p, Nil, typeFacetsCompletionPlugin)
            case _ => Nil
          }
        case _ => Nil
      }
    }
  }

  private def isWritingParamName(yPart: YPartBranch) = {
    yPart
      .ancestorOf(classOf[YMapEntry])
      .exists(p => p.key.asScalar.exists(s => Seq("baseUriParameters", "uriParameters").contains(s.text)))
  }
}
