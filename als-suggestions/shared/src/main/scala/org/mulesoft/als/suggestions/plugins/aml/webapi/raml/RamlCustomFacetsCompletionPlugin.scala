package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object RamlCustomFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RamlCustomFacetsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case s: Shape if params.yPartBranch.isKey && params.fieldEntry.isEmpty =>
        customFacets(s, Nil)
      case _ => Nil
    })
  }

  private def customFacets(s: Shape, traversed: Seq[String]): Seq[RawSuggestion] = {
    if (traversed.contains(s.id)) Nil
    else {
      val local = s.customShapePropertyDefinitions.flatMap(c => {
        if (c.range.isInstanceOf[NodeShape])
          c.name
            .option()
            .map(RawSuggestion.apply(_, isAKey = true, "unknown"))
        else c.name.option().map(RawSuggestion.forKey)
      })

      val inherited = s.linkTarget match {
        case Some(target: Shape) => customFacets(target, s.id +: traversed)
        case _                   => s.inherits.flatMap(customFacets(_, s.id +: traversed))
      }
      local ++ inherited
    }
  }
}
