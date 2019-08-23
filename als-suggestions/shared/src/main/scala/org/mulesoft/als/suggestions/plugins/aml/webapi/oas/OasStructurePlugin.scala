package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.Shape
import amf.dialects.OAS20Dialect
import amf.plugins.domain.webapi.models.{EndPoint, Parameter, WebApi}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionPlugin, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OasStructurePlugin extends AMLCompletionPlugin {
  override def id: String = "AMLStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Parameter | _: Shape                           => emptySuggestion
      case _: EndPoint if isInParameter(request.yPartBranch) => emptySuggestion
      case _: WebApi if request.yPartBranch.isKeyDescendanceOf("info") =>
        infoSuggestions(request.indentation)
      case _ => AMLStructureCompletionPlugin.resolve(request)
    }
  }

  def isInParameter(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyDescendanceOf("parameters")

  def infoSuggestions(indentation: String) =
    Future(OAS20Dialect.DialectNodes.InfoObject.propertiesRaw(indentation, Some("docs")))
}
