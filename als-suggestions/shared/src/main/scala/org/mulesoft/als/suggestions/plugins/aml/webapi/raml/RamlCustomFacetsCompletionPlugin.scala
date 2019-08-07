package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLSuggestionsHelper

import scala.concurrent.Future

object RamlCustomFacetsCompletionPlugin extends AMLCompletionPlugin with AMLSuggestionsHelper {
  override def id: String = "RamlCustomFacetsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case s: Shape if params.yPartBranch.isKey && params.fieldEntry.isEmpty =>
        CustomFacetFinder("\n" + getIndentation(params.baseUnit, params.position))
          .getCustomFacets(s, Nil)
      case _ => Nil
    })
  }

  private case class CustomFacetFinder(identation: String) {
    def getCustomFacets(s: Shape, traversed: Seq[String]): Seq[RawSuggestion] = {
      if (traversed.contains(s.id)) Nil
      else {
        val local = s.customShapePropertyDefinitions.flatMap(c => {
          if (c.range.isInstanceOf[NodeShape])
            c.name
              .option()
              .map(RawSuggestion.apply(_, identation, isAKey = true))
          else c.name.option().map(RawSuggestion.forKey)
        })

        val inherited = s.linkTarget match {
          case Some(target: Shape) => getCustomFacets(target, s.id +: traversed)
          case _                   => s.inherits.flatMap(getCustomFacets(_, s.id +: traversed))
        }
        local ++ inherited
      }
    }
  }
}
