package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.document.Document
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{Operation, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OperationTags extends AMLCompletionPlugin {
  override def id: String = "OperationTags"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val names =
        if (request.amfObject.isInstanceOf[Operation] && request.fieldEntry
              .exists(_.field == OperationModel.Tags) && request.yPartBranch.isInArray) {
          request.baseUnit match {
            case d: Document =>
              d.encodes match {
                case w: WebApi => w.tags.flatMap(_.name.option())
                case _         => Nil
              }
            case _ => Nil
          }
        } else Nil
      names.map(RawSuggestion(_, isAKey = false))
    }
  }
}
