package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.parser._
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

class OASRequiredObjectCompletionPlugin(ns: NodeShape, requireds: Seq[String]) {

  def resolve(): Seq[RawSuggestion] = {
    ns.properties
      .flatMap(_.name.option())
      .filter(p => !requireds.contains(p))
      .map(RawSuggestion(_, isAKey = false))
  }
}

object OASRequiredObjectCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "OASRequiredObjectCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      params.amfObject match {
        case ns: NodeShape => resolveNode(ns, params.yPartBranch)
        case _             => Nil
      }
    )
  }

  private def resolveNode(ns: NodeShape, yPartBranch: YPartBranch): Seq[RawSuggestion] = {

    yPartBranch.parentEntry match {
      case Some(e) if e.key.asScalar.map(_.text).contains("required") =>
        val required = e.value.toOption[Seq[String]].getOrElse(Nil)
        new OASRequiredObjectCompletionPlugin(ns, required).resolve()
      case _ => Nil
    }
  }
}
