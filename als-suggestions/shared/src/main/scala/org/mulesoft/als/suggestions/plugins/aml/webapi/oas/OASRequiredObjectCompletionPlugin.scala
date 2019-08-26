package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.{ExecutionContext, Future}

class OASRequiredObjectCompletionPlugin(ns: NodeShape) {

  def resolve(): Seq[RawSuggestion] = {
    ns.properties
      .flatMap(_.name.option())
      .map(RawSuggestion(_, isAKey = false))
  }
}

object OASRequiredObjectCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "OASRequiredObjectCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case ns: NodeShape => resolveNode(ns, params.yPartBranch)
        case _             => Nil
      }
    }(ExecutionContext.Implicits.global)
  }

  private def resolveNode(ns: NodeShape, yPartBranch: YPartBranch): Seq[RawSuggestion] = {

    if (yPartBranch.isKeyDescendanceOf("required") || ((yPartBranch.isValue || yPartBranch.isArray || (yPartBranch.stringValue == "x" && yPartBranch.isInArray)) && yPartBranch
          .parentEntryIs("required"))) {
      new OASRequiredObjectCompletionPlugin(ns).resolve()
    } else Nil
  }
}
