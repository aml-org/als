package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.models.{Payload, Server}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async20PayloadCompletionPlugin extends AMLCompletionPlugin with AsyncMediaTypePluginFinder {
  override def id: String = "Async20PayloadCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val branchStack = request.branchStack
      branchStack.headOption match {
        case Some(p: Payload)
            if request.yPartBranch.isKeyDescendantOf("payload") &&
              !branchStack.exists(_.isInstanceOf[Server]) =>
          request.amfObject match {
            case s: Shape =>
              if (p.schemaMediaType.isNullOrEmpty) {
                Async20TypeFacetsCompletionPlugin.resolveShape(s, branchStack)
              } else {
                findPluginForMediaType(p).resolveShape(s, branchStack)
              }
          }
        case _ => Seq()
      }
    }
  }

}
