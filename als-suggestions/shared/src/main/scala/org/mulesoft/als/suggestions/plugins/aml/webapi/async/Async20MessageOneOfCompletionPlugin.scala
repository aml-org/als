package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async20MessageOneOfCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Async20MessageOneOfCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      if (
        MessageKnowledge.isRootMessageBlock(request) &&
        request.yPartBranch.brothers.isEmpty &&
        request.yPartBranch.isInArray
      )
        Seq(RawSuggestion.arrayProp("oneOf", "Schema"))
      else Nil
    }

}
