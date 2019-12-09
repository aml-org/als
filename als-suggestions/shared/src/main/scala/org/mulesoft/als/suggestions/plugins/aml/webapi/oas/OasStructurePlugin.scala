package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.Shape
import amf.dialects.oas.nodes.AMLInfoObject
import amf.plugins.domain.webapi.models.{Parameter, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.Oas20StructurePlugin.emptySuggestion
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionPlugin, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OasStructurePlugin extends AMLCompletionPlugin {
  override def id: String = "AMLStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Parameter | _: Shape                                     => emptySuggestion
      case _: WebApi if request.yPartBranch.isKeyDescendanceOf("info") => infoSuggestions()
      case _ =>
        AMLStructureCompletionPlugin.resolve(request)
    }
  }

  def infoSuggestions() =
    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))
}
