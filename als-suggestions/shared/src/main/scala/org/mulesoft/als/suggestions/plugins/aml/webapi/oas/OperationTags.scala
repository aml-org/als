package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.api.WebApi
import amf.apicontract.client.scala.model.domain.{Operation, Tag}
import amf.core.client.scala.model.document.Document
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OperationTags extends AMLCompletionPlugin {
  override def id: String = "OperationTags"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    request.amfObject match {
      case _: Tag
          if request.branchStack.headOption
            .exists(_.isInstanceOf[Operation]) && request.yPartBranch.isArray || request.yPartBranch.isInArray =>
        tags(request)
      case _ => Nil
    }
  }

  private def tags(request: AmlCompletionRequest) = {
    val names = request.baseUnit match {
      case d: Document =>
        d.encodes match {
          case w: WebApi => w.tags.flatMap(_.name.option())
          case _         => Nil
        }
      case _ => Nil
    }
    names.map(RawSuggestion(_, isAKey = false))
  }
}
