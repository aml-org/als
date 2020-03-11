package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionPlugin

import scala.concurrent.Future

object Async20StructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AMLStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Shape if (isInPayload(request.branchStack)) => emptySuggestion
      case _                                              => AMLStructureCompletionPlugin.resolve(request)
    }
  }

  def isInPayload(branch: Seq[AmfObject]): Boolean = {
    branch.headOption match {
      case Some(_: Payload) => true
      case _                => false
    }
  }

}
