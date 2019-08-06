package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLSuggestionsHelper
import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}

import scala.concurrent.Future

object RamlCustomFacetsCompletionPlugin extends CompletionPlugin with AMLSuggestionsHelper {
  override def id: String = "RamlCustomFacetsCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
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
