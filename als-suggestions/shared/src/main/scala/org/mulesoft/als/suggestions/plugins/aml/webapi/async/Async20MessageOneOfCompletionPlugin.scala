package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async20MessageOneOfCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Async20MessageOneOfCompletionPlugin"

  private def hasSingleChild(yPartBranch: YPartBranch): Boolean = yPartBranch.stack(3) match {
    case entry: YMapEntry => entry.value.children.size <= 1
    case _                => false
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      if (MessageKnowledge.isRootMessageBlock(request) && hasSingleChild(request.yPartBranch))
        Seq(RawSuggestion.arrayProp("oneOf", "Schema"))
      else Nil
    }

}
