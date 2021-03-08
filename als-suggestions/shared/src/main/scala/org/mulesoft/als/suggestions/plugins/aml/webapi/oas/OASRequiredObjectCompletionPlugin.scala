package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.IsInsideRequired

import scala.concurrent.{ExecutionContext, Future}

trait OASLikeRequiredObjectCompletionPlugin extends AMLCompletionPlugin with IsInsideRequired {
  override def id: String = "OASRequiredObjectCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case ns: NodeShape => resolveNode(ns, params.yPartBranch)
        case _             => Nil
      }
    }(ExecutionContext.Implicits.global)
  }

  private def resolveNode(ns: NodeShape, yPartBranch: YPartBranch): Seq[RawSuggestion] =
    if (isInsideRequired(yPartBranch)) resolve(ns)
    else Nil

  private def resolve(ns: NodeShape): Seq[RawSuggestion] =
    ns.properties
      .flatMap(_.name.option())
      .map(RawSuggestion(_, isAKey = false))
}

object OASRequiredObjectCompletionPlugin extends OASLikeRequiredObjectCompletionPlugin
